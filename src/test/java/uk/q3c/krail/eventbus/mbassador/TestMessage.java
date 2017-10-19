package uk.q3c.krail.eventbus.mbassador;

/**
 * Created by David Sowerby on 19 Oct 2017
 */
public class TestMessage implements BusMessage {

    private String msg;

    public TestMessage(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
