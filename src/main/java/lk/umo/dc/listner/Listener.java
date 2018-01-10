package lk.umo.dc.listner;

/**
 * Created by dev on 11/10/16.
 */
public abstract class Listener {

    public abstract void initListener(int port);
    abstract String processMessage(String message);

}
