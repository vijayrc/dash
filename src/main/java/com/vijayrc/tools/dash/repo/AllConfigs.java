package com.vijayrc.tools.dash.repo;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.vijayrc.tools.dash.config.Category;
import com.vijayrc.tools.dash.config.Source;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.valueOf;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Vijay Chakravarthy
 * @version %I% %G%
 * @since 1.0
 */
@Repository
@Scope("singleton")
@SuppressWarnings("unchecked")
public class AllConfigs {

    private static Logger log = Logger.getLogger(AllConfigs.class);

    private String jsonDir;

    private String csvDir;

    private String configYaml;

    private List<Category> categories = new ArrayList<>();

    private List<Source> sources = new ArrayList<>();

    @Autowired
    public AllConfigs(@Value("${config.yaml}") String configYaml) throws Exception {
        this.configYaml = configYaml;
        init();
    }

    private void init() throws IOException {
//        ClassPathResource classPathResource = new ClassPathResource(configYaml);
//        final YamlReader reader = new YamlReader(new FileReader(classPathResource.getFile()));
        final YamlReader reader = new YamlReader(new FileReader(configYaml));
        final Map yaml = (Map) reader.read();

        setUpDirs(yaml);
        setupSSL(yaml);
        setupCategories(yaml);
        setupSources(yaml);

        log.info("initialized config " + toString());
    }

    private void setUpDirs(Map yaml) throws IOException {
        String baseDir = (String) yaml.get("baseDir");

        File base = new File(baseDir);
        jsonDir = baseDir + "/json";
        csvDir = baseDir + "/csv";

        if (!base.exists()) {
            base.mkdirs();
        }
        asList(jsonDir, csvDir).forEach(dir -> new File(dir).mkdir());
    }

    private void setupSources(Map yaml) {
        this.sources.clear();
        final List<Map<String, String>> sources = (List) yaml.get("sources");
        sources.forEach(map -> {

            boolean shouldFlatten = false;
            String flatten = map.get("flatten");
            if (isNotBlank(flatten)) shouldFlatten = valueOf(flatten);

            this.sources
                    .add(new Source(map.get("name"),
                            map.get("link"), map.get("poll"),
                            map.get("duration"), map.get("username"),
                            map.get("password"), shouldFlatten));
        });
    }

    private void setupCategories(Map yaml) {
        this.categories.clear();
        final List<Map<String, String>> categories = (List) yaml.get("categories");
        categories.forEach(map -> this.categories.add(new Category(map.get("name"), map.get("unit"), map.get("fields"))));
    }

    private void setupSSL(Map yaml) {
        System.setProperty("javax.net.ssl.trustStore", (String) yaml.get("sslTruststore"));
        System.setProperty("javax.net.ssl.trustStorePassword", (String) yaml.get("sslTruststorePassword"));
    }

    public Category getCategoryFor(String name) {
        for (Category category : categories)
            if (category.name.equalsIgnoreCase(name))
                return category;
        throw new RuntimeException("Category not found");
    }

    @Override
    public String toString() {
        return "AllConfigs [ jsonDir=" + jsonDir + "|csvDir=" + csvDir + "|categories=" + categories + "|sources=" + sources + "]";
    }

    public String getJsonDir() {
        return jsonDir;
    }

    public String getCsvDir() {
        return csvDir;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Source> getSources() {
        return sources;
    }

    public Source getSourceFor(String sourceName) {
        for (Source source : sources)
            if (source.getName().equalsIgnoreCase(sourceName))
                return source;
        throw new RuntimeException("Source not found");
    }
}
