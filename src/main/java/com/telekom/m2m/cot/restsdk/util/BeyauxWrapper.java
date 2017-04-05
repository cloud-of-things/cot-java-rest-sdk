package com.telekom.m2m.cot.restsdk.util;

import org.cometd.bayeux.client.ClientSession;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.ClientTransport;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.util.BasicAuthentication;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Patrick Steinert on 13.01.17.
 */
public class BeyauxWrapper {

    public static void main(String[] args) throws Exception {
        BeyauxWrapper b = new BeyauxWrapper();
        b.demo1();
    }

    public BeyauxWrapper() {


    }

    public void demo1() throws Exception {

        URI uri = new URI("https://dtagtest.ram.m2m.telekom.com/cep/realtime");
        String realm = "MyRealm";
        String user = "username";
        String pass = "password";
        // Instantiate and configure the SslContextFactory
        SslContextFactory sslContextFactory = new SslContextFactory();

        // Instantiate HttpClient with the SslContextFactory
        HttpClient client = new HttpClient(sslContextFactory);

        // Configure HttpClient, for example:
        client.setFollowRedirects(false);
        client.start();

        client.GET(uri);

        // Add authentication credentials
        AuthenticationStore auth = client.getAuthenticationStore();
        auth.addAuthentication(new BasicAuthentication(uri, realm, user, pass));

        Map<String, Object> options = new HashMap<String, Object>();

        ClientTransport clientTransport = new LongPollingTransport(options, client);
        ClientSession beyauxClient = new BayeuxClient(uri.toString(), clientTransport);

        beyauxClient.handshake();

    }
}
