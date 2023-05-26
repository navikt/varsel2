package no.nav.modig.common;

import javax.naming.ConfigurationException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Skaper og konfigurerer SporingsLogger
 */
public class SporingsLoggerFactory {

    private static SporingsLogger instance;

    public static void init(String configFil) throws Exception {
        instance = sporingsLogger(configFil);
    }

    public static void init(BufferedReader br) throws Exception {
        instance = sporingsLogger(br);
    }

    public static void init(InputStream stream) throws Exception {
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            reader = new InputStreamReader(stream);
            bufferedReader = new BufferedReader(reader);
            init(bufferedReader);
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static SporingsLogger getInstance() {
        return instance;
    }

    /**
     * lage en SporingsLogger basert på config data fra en bufferedReader
     */
    public static SporingsLogger sporingsLogger(BufferedReader br) throws Exception {
        return new SporingsLogger(getConfig(br));
    }

    /**
     * 
     * @param configFil
     *            for test er dette vanligvis src/test/resources/\<mitFilNavn\>
     */
    public static SporingsLogger sporingsLogger(String configFil) throws Exception {
        ClassLoader classLoader = SporingsLoggerFactory.class.getClassLoader();
        InputStream stream = classLoader.getResource(configFil).openStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

        SporingsLogger sporing = sporingsLogger(bufferedReader);

        bufferedReader.close();
        stream.close();
        return sporing;
    }

    static Map<Class<? extends Object>, KlasseConfig> getConfig(BufferedReader br) throws Exception {
        HashMap<Class<? extends Object>, KlasseConfig> newConfig = new HashMap<Class<? extends Object>, KlasseConfig>();
        String line = br.readLine();

        while (line != null) {
            String[] segments = line.split(",");
            String klasseNavn = segments[0];
            String idNavn = segments[1];
            String attributtPath = segments[2];

            KlasseConfig klasseConfig = KlasseConfig.makeKlasseConfig(klasseNavn, attributtPath, idNavn);
            if (newConfig.containsKey(klasseConfig.getKlasse())) {
                KlasseConfig kcOld = newConfig.get(klasseConfig.getKlasse());
                if (kcOld.getAttributtPaths().contains(attributtPath)) {
                    throw new ConfigurationException("Duplicate classname [" + klasseNavn + "] and attributePath [" + attributtPath + "]");
                }
                List<String> attributtPaths = kcOld.getAttributtPaths();
                attributtPaths.addAll(klasseConfig.getAttributtPaths());
                kcOld.setAttributtPaths(attributtPaths);
            } else {
                newConfig.put(klasseConfig.getKlasse(), klasseConfig);
            }

            line = br.readLine();
        }
        return newConfig;
    }
}
