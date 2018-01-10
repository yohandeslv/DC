package lk.umo.dc.existing;

import javafx.util.Pair;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

public class FileNode {
    private String bAddress;
    private int bPort;
    private int port;
    private String address;
    private String username;
    DatagramSocket socket;
    private List<Neighbour> neighbours;
    private List<String> files;
    private List<Pair<String, Neighbour>> messageRoutingHistory;

    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    public List<String> getFiles() {
        return this.files;
    }

    public List<Neighbour> getNeighbours() {
        return neighbours;
    }

    public FileNode(String bAddress, int bPort, String username) throws UnknownHostException {
        this.bAddress = bAddress;
        this.bPort = bPort;
        this.username = username;
        this.address = InetAddress.getLocalHost().getHostAddress();
        this.messageRoutingHistory = new ArrayList<Pair<String, Neighbour>>();
        this.LoadFiles();
        /*Assign a port number to this file node between 3000 and 1023.*/
        this.port = (int) (Math.random() * (3000 - 1023) + 1023);
        try {
            this.socket = new DatagramSocket(this.port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void Receive() {

        FileNodeCommand receiveCommand = new FileNodeCommand(this.socket) {
            @Override
            public void run() {
                while (true) {
                    String message = this.receive();
                    message = message.replace("\n", "");
                    System.out.println(message);
                    try {
                        String[] tokens = message.split(" ");
                        if (message.equals("RTTBL")) {
                            String replyMessage = "\n";
                            for (int i = 0; i < FileNode.this.neighbours.size(); i++) {
                                replyMessage += String.format("%1$s:%2$d\n", FileNode.this.neighbours.get(i).getIp(), FileNode.this.neighbours.get(i).getPort());
                            }
                            FileNode.this.SendMessage(this.getPacket().getAddress().getHostAddress(), this.getPacket().getPort(), replyMessage);
                        } else if (message.equals("FILES")) {
                            FileNode.this.SendMessage(this.getPacket().getAddress().getHostAddress(), this.getPacket().getPort(), String.join("\n", FileNode.this.getFiles()));
                        } else if (tokens[1].equals("REGOK") && this.getAddress().equals(FileNode.this.bAddress) && FileNode.this.bPort == this.getPort()) {
                            FileNode.this.neighbours = new ArrayList<Neighbour>();
                            for (int i = 0; i < tokens.length; i += 2) {
                                if (i > 2) {
                                    FileNode.this.Join(tokens[i - 1], Integer.parseInt(tokens[i]));
                                }
                            }
                        } else if (tokens[1].equals("UNREG")) {
                            FileNode.this.Unreg();
                        } else if (tokens[1].equals("UNROK") && this.getAddress().equals(FileNode.this.bAddress) && FileNode.this.bPort == this.getPort()) {
                            FileNode.this.Leave();
                        } else if (tokens[1].equals("JOIN")) {
                            FileNode.this.neighbours.add(
                                    new Neighbour(
                                            tokens[2],
                                            Integer.parseInt(tokens[3])));
                            FileNode.this.JoinOk(tokens[2], Integer.parseInt(tokens[3]));

                        } else if (tokens[1].equals("JOINOK")) {
                            FileNode.this.neighbours.add(
                                    new Neighbour(
                                            this.getPacket().getAddress().getHostAddress(),
                                            this.getPacket().getPort()));
                        } else if (tokens[1].equals("LEAVE")) {
                            Iterator<Neighbour> iterator = FileNode.this.neighbours.iterator();
                            while (iterator.hasNext()) {
                                Neighbour current = iterator.next();
                                if (current.getIp().equals(tokens[2])
                                        && current.getPort() == Integer.parseInt(tokens[3])) {
                                    iterator.remove();
                                    break;
                                }
                            }



                            FileNode.this.LeaveOk(tokens[2], Integer.parseInt(tokens[3]));
                        } else if (tokens[1].equals("LEAVEOK")) {
                            Iterator<Neighbour> iterator = FileNode.this.neighbours.iterator();
                            while (iterator.hasNext()) {
                                Neighbour current = iterator.next();
                                if (current.getIp().equals(this.getPacket().getAddress().getHostAddress())
                                        && current.getPort() == this.getPacket().getPort()) {
                                    iterator.remove();
                                    break;
                                }
                            }
                            if (FileNode.this.neighbours.size() == 0) {
                                break;
                            }
                        } else if (tokens[1].equals("SER")) {
                            List<String> matchingFiles;
                            String ip, fileName;
                            int sourcePort;
                            int hopCount;
                            String messageId;
                            if (tokens.length == 7) {
                                messageId = tokens[6];
                            } else {
                                messageId = UUID.randomUUID().toString();
                            }
                            if (tokens.length > 3) {
                                fileName = tokens[4];
                                matchingFiles = FileNode.this.SearchFile(fileName);
                                ip = tokens[2];
                                sourcePort = Integer.parseInt(tokens[3]);
                                hopCount = Integer.parseInt(tokens[5]);
                            } else {
                                fileName = tokens[2];
                                matchingFiles = FileNode.this.SearchFile(fileName);
                                ip = this.getPacket().getAddress().getHostAddress();
                                sourcePort = this.getPort();
                                hopCount = 10;
                            }

                            boolean messagePreviouslyFound = false;
                            for (int i = 0; i < FileNode.this.messageRoutingHistory.size(); i++) {
                                if (FileNode.this.messageRoutingHistory.get(i).getKey().equals(messageId)) {
                                    messagePreviouslyFound = true;
                                }
                            }

                            if ((matchingFiles.size() > 0  && !messagePreviouslyFound)|| (hopCount == 0 && matchingFiles.size() == 0) || FileNode.this.neighbours.size() == 0) {

                                FileNode.this.SearchOK(ip, sourcePort, hopCount, matchingFiles);
                            }

                            FileNode.this.messageRoutingHistory.add(new Pair<String, Neighbour>(messageId, new Neighbour(this.getPacket().getAddress().getHostAddress(), this.getPacket().getPort())));


                            if (hopCount > 0 && FileNode.this.neighbours.size() > 0) {
                                FileNode.this.Search(ip, sourcePort, fileName, hopCount - 1, messageId);
                            }
                        }

                    } catch (Exception ex) {
                        FileNode.this.Error(this.getPacket().getAddress().getHostAddress(), this.getPacket().getPort());
                    }
                }
            }
        };
        receiveCommand.start();
    }

    public void Reg() {
        String query = String.format("REG %1$s %2$d %3$s", this.address, this.port, this.username);
        System.out.println(query);
        query = String.format("%1$04d %2$s", query.length() + 5, query);
        FileNodeCommand command = new FileNodeCommand(this.socket);
        command.send(this.bAddress, this.bPort, query);
    }

    public void Unreg() {
        FileNodeCommand command = new FileNodeCommand(this.socket) {
            @Override
            public void run() {
                String query = String.format("UNREG %1$s %2$d %3$s", FileNode.this.address, FileNode.this.port, FileNode.this.username);
                query = String.format("%1$04d %2$s", query.length() + 5, query);
                this.send(FileNode.this.bAddress, FileNode.this.bPort, query);
            }
        };
        command.start();
    }

    public void Join(final String address, final int port) {
        FileNodeCommand command = new FileNodeCommand(this.socket) {
            @Override
            public void run() {
                String query = String.format("JOIN %1$s %2$d", FileNode.this.address, FileNode.this.port);
                query = String.format("%1$04d %2$s", query.length() + 5, query);
                this.send(address, port, query);
            }
        };
        command.start();
    }

    public void JoinOk(final String address, final int port) {
        FileNodeCommand command = new FileNodeCommand(this.socket) {
            @Override
            public void run() {
                String query = String.format("JOINOK 0");
                query = String.format("%1$04d %2$s", query.length() + 5, query);
                this.send(address, port, query);
            }
        };
        command.start();
    }

    public void Leave() {
        FileNodeCommand command = new FileNodeCommand(this.socket) {
            @Override
            public void run() {
                String query = String.format("LEAVE %1$s %2$d", FileNode.this.address, FileNode.this.port);
                query = String.format("%1$04d %2$s", query.length() + 5, query);
                for (int i = 0; i < FileNode.this.neighbours.size(); i++) {
                    this.send(FileNode.this.neighbours.get(i).getIp(), FileNode.this.neighbours.get(i).getPort(), query);
                }
            }
        };
        command.start();
    }

    public void LeaveOk(final String address, final int port) {
        FileNodeCommand command = new FileNodeCommand(this.socket) {
            @Override
            public void run() {
                String query = String.format("LEAVEOK  0");
                query = String.format("%1$04d %2$s", query.length() + 5, query);
                this.send(address, port, query);
            }
        };
        command.start();
    }

    public void Search(final String address, final int port, final String fileName, final int hopCount, final String messageId) {
        FileNodeCommand command = new FileNodeCommand(this.socket) {
            @Override
            public void run() {


                String query = String.format("SER %1$s %2$d %3$s %4$d %5$s", address, port, fileName, hopCount, messageId);
                query = String.format("%1$04d %2$s", query.length() + 5, query);
                System.out.println("Search Quary "+query);
                Iterator<Neighbour> neighbourIterator = FileNode.this.neighbours.iterator();
                Iterator<Pair<String, Neighbour>> routingHistoryIterator;
                List<Neighbour> notRoutedNeighbors = new ArrayList<Neighbour>();
                Neighbour currentNeighbour;
                Pair<String, Neighbour> currentRoutingHistory;
                boolean hasAlreadyRouted;
                while (neighbourIterator.hasNext()) {
                    currentNeighbour = neighbourIterator.next();
                    routingHistoryIterator = FileNode.this.messageRoutingHistory.iterator();
                    hasAlreadyRouted = false;
                    while (routingHistoryIterator.hasNext()) {
                        System.out.println("END TIME: "+System.currentTimeMillis());
                        currentRoutingHistory = routingHistoryIterator.next();
                        if (currentNeighbour.getIp().equals(currentRoutingHistory.getValue().getIp())
                                && currentNeighbour.getPort() == currentRoutingHistory.getValue().getPort()
                                && currentRoutingHistory.getKey().equals(messageId)) {
                            hasAlreadyRouted = true;
                            break;
                        }
                    }
                    if (!hasAlreadyRouted) {
                        notRoutedNeighbors.add(currentNeighbour);
                    }
                }
                Collections.shuffle(notRoutedNeighbors);

                if (notRoutedNeighbors.size() > 0) {
                    this.send(notRoutedNeighbors.get(0).getIp(), notRoutedNeighbors.get(0).getPort(), query);
                    FileNode.this.messageRoutingHistory.add(new Pair<>(messageId, notRoutedNeighbors.get(0)));
                } else {
                    Collections.shuffle(FileNode.this.neighbours);
                    this.send(FileNode.this.neighbours.get(0).getIp(), FileNode.this.neighbours.get(0).getPort(), query);
                }
            }
        };

        command.start();


    }

    public void SearchOK(final String address, final int port, final int hopCount, final List<String> fileNames) {
        FileNodeCommand command = new FileNodeCommand(this.socket) {
            @Override
            public void run() {
                List<String> replacedWithUnderscore = new ArrayList<String>();
                for (int i = 0; i < fileNames.size(); i++) {
                    replacedWithUnderscore.add(fileNames.get(i).replace(" ", "_"));
                }
                String query = String.format("SEROK %1$d %2$s %3$d %4$d %5$s", fileNames.size(), FileNode.this.address, FileNode.this.port, hopCount, String.join(" ", replacedWithUnderscore));
                query = String.format("%1$04d %2$s\n", query.length() + 5, query);
                this.send(address, port, query);
            }
        };
        command.start();
    }

    public void Error(final String address, final int port) {
        FileNodeCommand command = new FileNodeCommand(this.socket) {
            @Override
            public void run() {
                String query = String.format("ERROR\n");
                query = String.format("%1$04d %2$s", query.length() + 5, query);
                this.send(address, port, query);
            }
        };
        command.start();
    }

    public void SendMessage(final String address, final int port, final String message) {
        FileNodeCommand command = new FileNodeCommand(this.socket) {
            @Override
            public void run() {
                String query = String.format("%1$s\n", message);
                this.send(address, port, query);
            }
        };
        command.start();
    }

    private void LoadFiles() {
        List<String> allFiles = new ArrayList<String>(
                Arrays.asList("Adventures of Tintin",
                        "Jack and Jill",
                        "Glee",
                        "The Vampire Diarie",
                        "King Arthur",
                        "Windows XP",
                        "Harry Potter",
                        "Kung Fu Panda",
                        "Lady Gaga",
                        "Twilight",
                        "Windows 8",
                        "Mission Impossible",
                        "Turn Up The Music",
                        "Super Mario",
                        "American Pickers",
                        "Microsoft Office 2010",
                        "Happy Feet",
                        "Modern Family",
                        "American Idol",
                        "Hacking for Dummies",
                       "Adventures of Tintin1",
                        "Jack and Jill1",
                        "Glee1",
                        "The Vampire Diarie1",
                        "King Arthur1",
                        "Windows XP1",
                        "Harry Potter1",
                        "Kung Fu Panda1",
                        "Lady Gaga1",
                        "Twilight1",
                        "Windows 81",
                        "Mission Impossible1",
                        "Turn Up The Music1",
                        "Super Mario1",
                        "American Pickers1",
                        "Microsoft Office 20101",
                        "Happy Feet1",
                        "Modern Family1",
                        "American Idol1",
                        "Hacking for Dummies1",
                        "Adventures of Tintin2",
                        "Jack and Jill2",
                        "Glee2",
                        "The Vampire Diarie2",
                        "King Arthur2",
                        "Windows XP2",
                        "Harry Potter2",
                        "Kung Fu Panda2",
                        "Lady Gaga2",
                        "Twilight2",
                        "Windows 82",
                        "Mission Impossible2",
                        "Turn Up The Music2",
                        "Super Mario2",
                        "American Pickers2",
                        "Microsoft Office 20102",
                        "Happy Feet2",
                        "Modern Family2",
                        "American Idol2",
                        "Hacking for Dummies2",
                        "Adventures of Tintin3",
                        "Jack and Jill3",
                        "Glee3",
                        "The Vampire Diarie3",
                        "King Arthur3",
                        "Windows XP3",
                        "Harry Potter3",
                        "Kung Fu Panda3",
                        "Lady Gaga3",
                        "Twilight3",
                        "Windows 83",
                        "Mission Impossible3",
                        "Turn Up The Music3",
                        "Super Mario3",
                        "American Pickers3",
                        "Microsoft Office 20103",
                        "Happy Feet3",
                        "Modern Family3",
                        "American Idol3",
                        "Hacking for Dummies3")
        );
        Collections.shuffle(allFiles);
        this.files = allFiles.subList(0, 10);
    }

    private List<String> SearchFile(String query) {
        List<String> matchingFiles = new ArrayList<String>();
        String[] tokens = query.split("_");
        int matchWordCount = 0;
        for (int j = 0; j < this.files.size(); j++) {
            String[] words = this.files.get(j).split(" ");
            matchWordCount = 0;
            for (int i = 0; i < tokens.length; i++) {
                for (int z = 0; z < words.length; z++) {
                    if (tokens[i].toLowerCase().equals(words[z].toLowerCase())) {
                        matchWordCount++;
                    }
                }
            }
            if (matchWordCount == tokens.length) {
                matchingFiles.add(this.files.get(j));
            }
        }
        return matchingFiles;
    }

    public static void main(String[] args) throws UnknownHostException {
        FileNode fileNode = new FileNode(args[0], Integer.parseInt(args[1]), args[2]);
        fileNode.Receive();
        fileNode.Reg();

        commandListner(fileNode);

    }

    private static void commandListner(FileNode fileNode) {
        while (true) {
            System.out.println("Waiting for queries..");
            Scanner scan = new Scanner(System.in);
            String input = scan.nextLine();
            String[] tokens = input.split(" ");
            int token_count = tokens.length;
            if (token_count > 1) {
                 /*for (int i=0; i<token_count;i++) {
                    System.out.println(tokens[i]);
                }*/
                //this input should be processed
                if (tokens[1].equals("REG")) {
                    //register with bootstrap
                    fileNode.Reg();
                    System.out.println("REG issued");
                }
                else if (tokens[1].equals("UNREG")) {
                    fileNode.Unreg();
                    System.out.println("UNREG issued");
                }
                else if (tokens[1].equals("JOIN")) {
                    //join with other node
                    System.out.println("JOIN issued");
                    fileNode.neighbours.add(
                            new Neighbour(
                                    tokens[2],
                                    Integer.parseInt(tokens[3])));
                    fileNode.JoinOk(tokens[2], Integer.parseInt(tokens[3]));
                }
                else if (tokens[1].equals("LEAVE")) {
                    //leave the system
                    fileNode.Leave();
//                    fileNode.LeaveOk(fileNode.getAddress(), fileNode.getPort());
                }
                else if (tokens[1].equals("SHOW")){

//                            FileNode.this.SendMessage(this.getPacket().getAddress().getHostAddress(), this.getPacket().getPort(), String.join("\n", FileNode.this.getFiles()));

                    List<String> files =  fileNode.getFiles();

                    for (String filename : files){
                        System.out.println(filename);
                    }

                }
                else if (tokens[1].equals("SER")) {
                    //search
                    List<String> matchingFiles = null;
                    String ip = "", fileName;
                    int sourcePort;
                    int hopCount = 0;
                    String messageId = UUID.randomUUID().toString();


                        fileName = tokens[2];
                        matchingFiles = fileNode.SearchFile(fileName);
                        ip = fileNode.getAddress();
                        sourcePort = fileNode.getPort();
                        hopCount = Integer.parseInt(tokens[3]);


                    if(matchingFiles.size() > 0){
                        fileNode.SearchOK(ip,sourcePort,hopCount,matchingFiles);
                    }


                    if (hopCount > 0 && fileNode.neighbours.size() > 0) {
                        fileNode.Search(ip, sourcePort, fileName, hopCount, messageId);
                    }
                    System.out.println("START TIME: "+System.currentTimeMillis());
                }

            }
            else if (input.equalsIgnoreCase("exit")) {
                //send unreg message
                fileNode.Unreg();
                break;
            }

        }
        System.exit(0);
    }
}
