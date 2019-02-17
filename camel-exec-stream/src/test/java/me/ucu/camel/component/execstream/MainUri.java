package me.ucu.camel.component.execstream;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * Created by Rui Zhou on 2019/2/17.
 */
public class MainUri {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from("exec-stream:out?command=cmd.exe /c tail -f c:\\tmp\\log.txt")
                        .to("log:exec-stream")
                        .to("stream:out");
            }
        });
        context.start();
        synchronized (MainUri.class) {
            MainUri.class.wait();
        }
    }
}
