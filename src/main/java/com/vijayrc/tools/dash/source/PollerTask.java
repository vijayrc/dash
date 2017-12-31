package com.vijayrc.tools.dash.source;

import com.vijayrc.tools.dash.config.Source;
import com.vijayrc.tools.dash.parse.Flattener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.TimerTask;

import static java.lang.Integer.parseInt;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.apache.commons.io.FileUtils.write;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Vijay Chakravarthy@vijayrc.com
 * @version %I% %G%
 * @since 1.0
 */
public class PollerTask extends TimerTask {

    private static Logger log = Logger.getLogger(PollerTask.class);

    private final Source source;
    private final File jsonDump;
    private final ObjectMapper mapper;

    private HttpGet httpget;
    private Flattener flattener;
    private LocalDateTime startTime;
    private CloseableHttpClient httpClient;

    public PollerTask(Source source, String jsonDir, String runName) {
        this.source = source;
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        this.jsonDump = new File(jsonDir + "/" + runName + "-" + source.getName() + ".txt");
        try {
            if (!jsonDump.exists()) jsonDump.createNewFile();
        } catch (IOException e) {
            log.error(getFullStackTrace(e));
            throw new RuntimeException(e);
        }
        this.startTime = LocalDateTime.now();
        this.flattener = new Flattener();
        log.info("poller spawned for " + source + " writing to " + jsonDump.getAbsolutePath());
    }


    @Override
    public void run() {
        if (exceededMaxDuration()) {
            log.warn("ran for max duration, so no more polling!");
            return;
        }
        try {
            if (httpClient == null) {
                httpClient = getHttpClient();
                httpget = new HttpGet(source.getLink());
            }
            CloseableHttpResponse response = httpClient.execute(httpget);
            String content = EntityUtils.toString(response.getEntity());
            if (source.isFlatten())
                content = flattener.flatten(content);

            log.info(source.getName() + "->" + jsonDump.getAbsolutePath());
            write(jsonDump, mapper.writeValueAsString(mapper.readValue(content, Object.class)) + "\n", true);
        } catch (Exception e) {
            log.error(getFullStackTrace(e));
        }
    }

    private CloseableHttpClient getHttpClient() {
        HttpClientBuilder builder = HttpClients.custom();
        builder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

        if (isNotBlank(source.getUsername())) {
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                    new AuthScope(source.getHost(), parseInt(source.getPort())),
                    new UsernamePasswordCredentials(source.getUsername(), source.getPassword()));
            builder.setDefaultCredentialsProvider(credsProvider);
        }
        return builder.build();
    }

    private boolean exceededMaxDuration() {
        return SECONDS.between(startTime, LocalDateTime.now()) > source.getDuration();
    }

}
