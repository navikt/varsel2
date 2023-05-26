package no.nav.modig.common;

import no.nav.modig.common.KlasseConfig.NestedMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Logger info om at objektet har blivit brukt, basert på konfigurasjon. For beskrivelse om krav se
 * http://confluence.adeo.no/display/Modernisering/Sporbarhetslogging
 */
public class SporingsLogger {
    private static final Logger log = LoggerFactory.getLogger(SporingsLogger.class);

    final Map<Class<? extends Object>, KlasseConfig> config;

    SporingsLogger(Map<Class<? extends Object>, KlasseConfig> config) {
        this.config = config;
    }

    /**
     * Logger info om at objektet har blivit brukt, basert på konfigurasjon
     */
    public void logg(Object dataObjekt, SporingsAksjon aksjon) {
        Class<? extends Object> klasse = dataObjekt.getClass();
        KlasseConfig klasseConfig = config.get(klasse);
        if (klasseConfig == null) {
            log.debug("Finner ikke noen konfigurasjon for klassen {} så logger den ikke", klasse.getName());
            return;
        }

        String aksjonString = aksjon.toString();
        for (String pathNode : klasseConfig.getAttributtPaths()) {
            try {
                NestedMethod nested = klasseConfig.getNestedIdMethod();
                Object idObjekt = dataObjekt;
                // walk down the list of nestedMethods and dataobject in sync, fetching new objects at each step
                while (!nested.isLeaf()) {
                    idObjekt = nested.getMethod().invoke(idObjekt);
                    nested = nested.getNode();
                }
                doLogg(aksjonString, klasseConfig.getKlasseNavn(), pathNode, nested.getMethod().invoke(idObjekt));

            } catch (IllegalAccessException e) {
                doLogg(aksjonString, klasseConfig.getKlasseNavn(), pathNode, "Unable to fetch object id");
                log.error("Unable to fetch object id from {},{}", klasseConfig.getKlasseNavn(), klasseConfig.getIdNavn());
            } catch (IllegalArgumentException e) {
                doLogg(aksjonString, klasseConfig.getKlasseNavn(), pathNode, "Unable to fetch object id");
                log.error("Unable to fetch object id from {},{}", klasseConfig.getKlasseNavn(), klasseConfig.getIdNavn());
            } catch (InvocationTargetException e) {
                doLogg(aksjonString, klasseConfig.getKlasseNavn(), pathNode, "Unable to fetch object id");
                log.error("Unable to fetch object id from {},{}", klasseConfig.getKlasseNavn(), klasseConfig.getIdNavn());
            }
        }
    }

    /**
     * logg formatet på sporingslogging
     */
    void doLogg(String aksjon, String objektNavn, String attributePath, Object objektId) {
        log.info("{} {}.{} {}", aksjon, objektNavn, attributePath, objektId);
    }
}

/**
 * Dataobjekt for å lagre informatsjon om en klasse og hva som skal logges
 */
final class KlasseConfig {
    private final String klasseNavn;
    private Class<? extends Object> klasse;
    private List<String> attributtPaths = new ArrayList<String>();
    private String idNavn;
    private NestedMethod nestedIdMethod;

    public String getKlasseNavn() {
        return klasseNavn;
    }

    public Class<? extends Object> getKlasse() {
        return klasse;
    }

    public List<String> getAttributtPaths() {
        return attributtPaths;
    }

    public void setAttributtPaths(List<String> attributtPaths) {
        this.attributtPaths = attributtPaths;
    }

    private void addToAttributtPath(String attributt) {
        attributtPaths.add(attributt);
    }

    public String getIdNavn() {
        return idNavn;
    }

    public NestedMethod getNestedIdMethod() {
        return nestedIdMethod;
    }

    private void setNestedIdMethod(NestedMethod nested) {
        this.nestedIdMethod = nested;
    }

    private KlasseConfig(String klasseNavn, String idNavn) {
        this.klasseNavn = klasseNavn;
        this.idNavn = idNavn;
    }

    private void setKlasse(Class<? extends Object> klasse) {
        this.klasse = klasse;
    }

    static KlasseConfig makeKlasseConfig(String klasseNavn, String attributt, String idNavn) throws ClassNotFoundException {
        KlasseConfig kc = new KlasseConfig(klasseNavn, idNavn);

        try {
            kc.setKlasse(Class.forName(klasseNavn));
        } catch (Exception e) {
            throw new ClassNotFoundException("Class not found: " + klasseNavn, e);
        }

        try {
            List<String> idSegments = new ArrayList<String>(Arrays.asList(idNavn.split("\\.")));
            kc.setNestedIdMethod(makeIdMethodClass(idSegments, kc.klasse));
        } catch (Exception e) {
            throw new ClassNotFoundException("Method for class " + klasseNavn + " not found: " + idNavn, e);
        }
        kc.addToAttributtPath(attributt);

        return kc;
    }

    static NestedMethod makeIdMethodClass(List<String> idSegments, Class<? extends Object> currentKlasse) throws NoSuchMethodException {
        if (idSegments.size() == 1) {
            Method method = currentKlasse.getMethod(idSegments.get(0));
            return new NestedMethod(method);
        }
        String root = idSegments.remove(0);
        Method method = currentKlasse.getMethod(root);
        NestedMethod inner = makeIdMethodClass(idSegments, method.getReturnType());
        return new NestedMethod(inner, method);
    }

    /*
     * Holds a list of further NestedMethods or a leaf, all with pointers to the methods to invoke on objects
     * 
     * To avoid doing recursion for each object that is being logged we do the recursion on the configured method
     */
    static class NestedMethod {
        private final boolean isLeaf;
        private final Method method;
        private final NestedMethod node;

        public NestedMethod(Method method) {
            this.method = method;
            isLeaf = true;
            node = null;
        }

        public NestedMethod(NestedMethod node, Method method) {
            this.method = method;
            this.node = node;
            isLeaf = false;
        }

        public boolean isLeaf() {
            return isLeaf;
        }

        public Method getMethod() {
            return method;
        }

        public NestedMethod getNode() {
            return node;
        }
    }
}
