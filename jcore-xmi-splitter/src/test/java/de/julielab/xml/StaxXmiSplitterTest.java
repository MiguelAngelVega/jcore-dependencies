package de.julielab.xml;

import static org.assertj.core.api.Assertions.*;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;

import de.julielab.jcore.types.*;
import de.julielab.xml.util.XMISplitterException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

import de.julielab.jcore.types.pubmed.Header;
import de.julielab.jcore.types.test.OtherToken;

import static org.junit.Assert.*;

public class StaxXmiSplitterTest {

    public static final HashSet<String> BASE_DOCUMENT_ANNOTATIONS = new HashSet<>(Arrays.asList("de.julielab.jcore.types.AbstractSectionHeading", "de.julielab.jcore.types.AbstractSection", "de.julielab.jcore.types.AbstractText"));

    private static final Logger log = LoggerFactory.getLogger(StaxXmiSplitterTest.class);

    private static final String SRCTR = "src/test/resources/";

    private static final String TEST_XMI = "src/test/resources/semedico.xmi";

    private static final String DOC = "documents";

    private static final String TPREF = "de.julielab.jcore.types";

    private static final String SENTTYPE = "de.julielab.jcore.types.Sentence";
    private static final String TOKTYPE = "de.julielab.jcore.types.Token";
    private static final String POSTYPE = "de.julielab.jcore.types.PennBioIEPOSTag";

    private static final String DOCBIN = "src/test/resources/documentBytes.bin";
    private static final String TOKBIN = "src/test/resources/tokenBytes.bin";
    private static final String SENTBIN = "src/test/resources/sentenceBytes.bin";
    private static final String POSBIN = "src/test/resources/posBytes.bin";

    private static final String NAMESPACEMAP_BIN = "src/test/resources/namespacemap.bin";

    private static final String TEST_AE_DESCRIPTOR = "src/test/resources/TestAEDescriptor.xml";

    private static final String TT = TPREF + ".Token";

    private static final String TS = TPREF + ".Sentence";

    private static final String TMM = TPREF + ".MeshMention";

    private static final String TDC = TPREF + ".DocumentClass";

    private static final String TAD = TPREF + ".AutoDescriptor";

    private static final String TDR = TPREF + ".DependencyRelation";

    private static final String TPOS = TPREF + ".PennBioIEPOSTag";

    private static final String TCNP = TPREF + ".ChunkNP";

    private static final Map<String, Integer> defaultSofaIdMap = new HashMap<>();

    static {
        defaultSofaIdMap.put("_InitialView", 1);
    }

    /**
     * Generates the test data for the XMIBuilder test by splitting the test xmi
     * file and then storing the byte arrays to file.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        List<String> elementsToStore = Lists.newArrayList(TOKTYPE, SENTTYPE, POSTYPE);
        CAS cas = getTestCAS();
        StaxXmiSplitter xmiSplitter = new StaxXmiSplitter(elementsToStore, true, true, DOC, BASE_DOCUMENT_ANNOTATIONS);
        // When storing the base document, begin with 0.
        int nextPossibleId = 0;
        byte[] b = FileUtils.readFileToByteArray(new File(TEST_XMI));

        XmiSplitterResult result = xmiSplitter.process(b, cas.getJCas(), nextPossibleId, defaultSofaIdMap);
        Map<String, ByteArrayOutputStream> xmiData = (Map<String, ByteArrayOutputStream>) result.xmiData;
        HashMap<String, String> namespaceMap = (HashMap<String, String>) result.namespaces;
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(NAMESPACEMAP_BIN))) {
            oos.writeObject(namespaceMap);
        }
        log.info("Creating test data.");
        try (ByteArrayOutputStream docOS = xmiData.get(DOC);
             ByteArrayOutputStream tokenOS = xmiData.get(TOKTYPE);
             ByteArrayOutputStream sentenceOS = xmiData.get(SENTTYPE);
             ByteArrayOutputStream posOS = xmiData.get(POSTYPE);
             FileOutputStream docBytesFile = new FileOutputStream(DOCBIN);
             FileOutputStream tokenBytesFile = new FileOutputStream(TOKBIN);
             FileOutputStream sentenceBytesFile = new FileOutputStream(SENTBIN);
             FileOutputStream posBytesFile = new FileOutputStream(POSBIN)) {

            docBytesFile.write(docOS.toByteArray());
            tokenBytesFile.write(tokenOS.toByteArray());
            sentenceBytesFile.write(sentenceOS.toByteArray());
            posBytesFile.write(posOS.toByteArray());

            log.debug("{}", new String(docOS.toByteArray()));
            log.debug("{}", new String(sentenceOS.toByteArray()));
            log.debug("{}", new String(tokenOS.toByteArray()));
            log.info("Finished.");
        }
    }

    private static CAS getTestCAS() throws ResourceInitializationException, IOException, InvalidXMLException {
        XMLInputSource input = new XMLInputSource(TEST_AE_DESCRIPTOR);
        ResourceSpecifier resourceSpecifier = UIMAFramework.getXMLParser().parseResourceSpecifier(input);
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(resourceSpecifier);
        CAS cas = CasCreationUtils.createCas(ae.getAnalysisEngineMetaData());
        return cas;
    }

    @Test
    public void testSplitterNewDocument() throws Exception {
        List<String> elementsToStore = Lists.newArrayList(TOKTYPE, SENTTYPE);
        CAS cas = getTestCAS();
        StaxXmiSplitter xmiSplitter = new StaxXmiSplitter(elementsToStore, true, true, DOC, BASE_DOCUMENT_ANNOTATIONS);
        // New document: Begin at zero.
        int nextPossibleId = 0;
        byte[] b = FileUtils.readFileToByteArray(new File(TEST_XMI));
        XmiSplitterResult result = xmiSplitter.process(b, cas.getJCas(), nextPossibleId, defaultSofaIdMap);
        LinkedHashMap<String, ByteArrayOutputStream> xmiData = result.xmiData;
        assertNotNull(xmiData);
        assertEquals(3, xmiData.size());
        assertNotNull(xmiData.get(DOC));
        assertNotNull(xmiData.get(TOKTYPE));
        assertNotNull(xmiData.get(SENTTYPE));

        // assertNotNull(xmiData.get(TDR));
        // assertNotNull(xmiData.get(TPOS));
        assembleSplitDocument(elementsToStore, result, xmiData);
    }

    private ByteArrayOutputStream assembleSplitDocument(List<String> elementsToStore, XmiSplitterResult result,
                                                        LinkedHashMap<String, ByteArrayOutputStream> xmiData) throws ResourceInitializationException, IOException, InvalidXMLException, SAXException {
        // Re-assembly-test.
        XmiBuilder builder = new XmiBuilder(result.namespaces, elementsToStore.toArray(new String[0]));
        LinkedHashMap<String, InputStream> inputData = new LinkedHashMap<>();
        for (Entry<String, ByteArrayOutputStream> entry : xmiData.entrySet()) {
            byte[] ba = entry.getValue().toByteArray();
            inputData.put(entry.getKey(), new ByteArrayInputStream(ba));
        }
        CAS cas2 = getTestCAS();
        ByteArrayOutputStream assembledXmi = builder.buildXmi(inputData, DOC, cas2.getTypeSystem());
        //System.out.println(new String(assembledXmi.toByteArray()));
        XmiCasDeserializer.deserialize(new ByteArrayInputStream(assembledXmi.toByteArray()), cas2);

        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        XmiCasSerializer.serialize(cas2, bas);

        return assembledXmi;
    }

    @Test
    public void testSplitterUpdatedDocumentAndCircles() throws Exception {
        // This test tests two things:
        // 1. If the XMI ID can be set to higher value than 0. This would be the case when we update a document.
        // 2. Tokens and DependencyRelations are mutual features of each other, thus building a circle. Make sure that
        //    a) The circles don't crash the splitter when resolving annotations recursively
        //    b) Tokens and DependencyRelations are still stored separatly
        List<String> elementsToStore = Lists.newArrayList(SENTTYPE, TOKTYPE, TDR);
        CAS cas = getTestCAS();
        StaxXmiSplitter xmiSplitter = new StaxXmiSplitter(elementsToStore, true, true, DOC, BASE_DOCUMENT_ANNOTATIONS);
        // Updated document. This value is nonsense in relation to the actual
        // document, it has much more elements than 15, but it will do for the
        // test.
        int nextPossibleId = 15;
        byte[] b = FileUtils.readFileToByteArray(new File(TEST_XMI));
        XmiSplitterResult result = xmiSplitter.process(b, cas.getJCas(), nextPossibleId, defaultSofaIdMap);
        LinkedHashMap<String, ByteArrayOutputStream> xmiData = result.xmiData;
        assertNotNull(xmiData);
        assertEquals(4, xmiData.size());
        assertNotNull(xmiData.get(DOC));
        assertNotNull(xmiData.get(SENTTYPE));
        assertNotNull(xmiData.get(TOKTYPE));
        assertNotNull(xmiData.get(TDR));

        // Since we store tokens and dependency relations separately, we don't expect one to occur in the data
        // of the other.
        assertEquals(-1, new String(xmiData.get(TOKTYPE).toByteArray()).indexOf("types:DependencyRelation"));
        assertEquals(-1, new String(xmiData.get(TDR).toByteArray()).indexOf("types:Token"));

        // Lets check whether we can assemble what we just split.
        ByteArrayOutputStream assembleSplitDocument = assembleSplitDocument(elementsToStore, result, xmiData);
        cas.reset();
        XmiCasDeserializer.deserialize(new ByteArrayInputStream(assembleSplitDocument.toByteArray()), cas);
        // Deserialization works. Check the contents.
        Collection<Token> tokens = JCasUtil.select(cas.getJCas(), Token.class);
        assertThat(tokens).isNotEmpty();
        for (Token token : tokens) {
            // each token should be part of the dependency tree
            assertThat(token.getDepRel()).hasSize(1);
            Stream.of(token.getDepRel().toArray()).forEach(rel -> assertThat(rel).isNotNull());
        }

        // Now make tests concerning the order of the annotations. This has been important in the past.
        // It's not any more, so failures at this point can just be regarded and the respective testing lines can
        // be removed, if necessary.
        String xmi = new String(assembleSplitDocument.toByteArray());
        assertTrue("Sentence before Token", xmi.indexOf("Sentence") < xmi.indexOf("Token"));
        assertTrue("Sentence before DependencyRelation", xmi.indexOf("Sentence") < xmi.indexOf("DependencyRelation"));


        // Just to be sure: serialize and deserialize twice to be sure we have created valid XMI
        CAS cas2 = getTestCAS();
        XmiCasDeserializer.deserialize(new ByteArrayInputStream(xmi.getBytes()), cas2);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmiCasSerializer.serialize(cas2, baos);
        cas2.reset();
        // Check that deserialization still works
        XmiCasDeserializer.deserialize(new ByteArrayInputStream(baos.toByteArray()), cas2);
    }

    @Test
    public void testNonRecursive() throws Exception {
        List<String> elementsToStore = Lists.newArrayList(SENTTYPE, TOKTYPE);
        CAS cas = getTestCAS();
        StaxXmiSplitter xmiSplitter = new StaxXmiSplitter(elementsToStore, false, true, DOC, BASE_DOCUMENT_ANNOTATIONS);
        // Updated document. This value is nonsense in relation to the actual
        // document, it has much more elements than 15, but it will do for the
        // test.
        int nextPossibleId = 0;
        byte[] b = FileUtils.readFileToByteArray(new File(TEST_XMI));
        XmiSplitterResult result = xmiSplitter.process(b, cas.getJCas(), nextPossibleId, defaultSofaIdMap);
        LinkedHashMap<String, ByteArrayOutputStream> xmiData = result.xmiData;
        assertNotNull(xmiData);
        assertEquals(3, xmiData.size());
        assertNotNull(xmiData.get(DOC));
        assertNotNull(xmiData.get(SENTTYPE));
        assertNotNull(xmiData.get(TOKTYPE));

        // Since we store non-recursively, we don't expect one annotation to occur in the data
        // of the other.
        assertEquals(-1, new String(xmiData.get(TOKTYPE).toByteArray()).indexOf("types:DependencyRelation"));
        // Also, since we do not store recursively, we expect superfluous FSArrays to be removed.
        assertEquals(-1, new String(xmiData.get(TOKTYPE).toByteArray()).indexOf("cas:FSArray"));

        // Lets check whether we can assemble what we just split.
        ByteArrayOutputStream assembleSplitDocument = assembleSplitDocument(elementsToStore, result, xmiData);
        cas.reset();
        XmiCasDeserializer.deserialize(new ByteArrayInputStream(assembleSplitDocument.toByteArray()), cas);
        // Deserialization works. Check the contents.
        Collection<Token> tokens = JCasUtil.select(cas.getJCas(), Token.class);
        assertThat(tokens).isNotEmpty();
        for (Token token : tokens) {
            // We didn't store the dependenc relations, so they should be gone by now.
            assertThat(token.getDepRel()).isNull();
        }


    }

    @Test
    public void testTypeCirclesNonRecursively() throws Exception {
        // This test checks if also the case of non-recursive storage works as expected, especially with circles
        // (Token and DependencyRelation). Note that there are no circles on annotation level: tokens point to
        // dependency relations and those have heads which in turn are tokens. Thus, the circle appears on the type
        // level.
        List<String> elementsToStore = Lists.newArrayList(SENTTYPE, TOKTYPE, TDR);
        CAS cas = getTestCAS();
        StaxXmiSplitter xmiSplitter = new StaxXmiSplitter(elementsToStore, false, true, DOC, BASE_DOCUMENT_ANNOTATIONS);

        int nextPossibleId = 0;
        byte[] b = FileUtils.readFileToByteArray(new File(TEST_XMI));
        XmiSplitterResult result = xmiSplitter.process(b, cas.getJCas(), nextPossibleId, defaultSofaIdMap);
        LinkedHashMap<String, ByteArrayOutputStream> xmiData = result.xmiData;
        assertNotNull(xmiData);
        assertEquals(4, xmiData.size());
        assertNotNull(xmiData.get(DOC));
        assertNotNull(xmiData.get(SENTTYPE));
        assertNotNull(xmiData.get(TOKTYPE));
        assertNotNull(xmiData.get(TDR));
        // Since we store non-recursively, we don't expect one annotation to occur in the data
        // of the other.
        assertEquals(-1, new String(xmiData.get(TOKTYPE).toByteArray()).indexOf("types:DependencyRelation"));
        assertEquals(-1, new String(xmiData.get(TDR).toByteArray()).indexOf("types:Token"));

        // Lets check whether we can assemble what we just split.
        ByteArrayOutputStream assembleSplitDocument = assembleSplitDocument(elementsToStore, result, xmiData);
        cas.reset();
        XmiCasDeserializer.deserialize(new ByteArrayInputStream(assembleSplitDocument.toByteArray()), cas);
        // Deserialization works. Check the contents. We should still find the circles between tokens and
        // dependency relations
        Collection<Token> tokens = JCasUtil.select(cas.getJCas(), Token.class);
        assertThat(tokens).isNotEmpty();
        boolean headFound = false;
        for (Token token : tokens) {
            // each token should be part of the dependency tree
            assertNotNull(token.getDepRel());
            assertThat(token.getDepRel()).hasSize(1);
            Stream.of(token.getDepRel().toArray()).forEach(rel -> assertThat(rel).isNotNull());
            DependencyRelation depRel = token.getDepRel(0);
            if (depRel.getHead() != null)
                headFound = true;
        }
        assertTrue(headFound);
    }

    @Test
    public void testBuilderIncompleteData() throws Exception {
        CAS aCAS = getTestCAS();
        LinkedHashMap<String, InputStream> xmiData = loadTestXmiByteData();
        Map<String, String> namespaces = loadNamespaceMap();
        XmiBuilder xmiBuilder = new XmiBuilder(namespaces, new String[]{TOKTYPE, SENTTYPE});
        xmiBuilder.setXMIStartElementData(namespaces);
        ByteArrayOutputStream xmiOS = xmiBuilder.buildXmi(xmiData, DOC, aCAS.getTypeSystem());
        byte[] b = xmiOS.toByteArray();

        // Useful to quickly view the created output in development.
        // FileOutputStream fos = new
        // FileOutputStream("src/test/resources/tmp.xmi");
        // fos.write(b);
        // fos.close();

        assertNotNull(b);
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        XmiCasDeserializer.deserialize(bais, aCAS);
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> loadNamespaceMap() throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(NAMESPACEMAP_BIN))) {
            return (Map<String, String>) ois.readObject();
        }
    }

    private LinkedHashMap<String, InputStream> loadTestXmiByteData() throws Exception {
        LinkedHashMap<String, InputStream> xmiData = new LinkedHashMap<>();
        InputStream docIS = new FileInputStream(DOCBIN);
        InputStream tokenIS = new FileInputStream(TOKBIN);
        InputStream sentenceIS = new FileInputStream(SENTBIN);
        // InputStream posIS = new FileInputStream(POSBIN);

        xmiData.put(DOC, docIS);
        xmiData.put(SENTTYPE, sentenceIS);
        xmiData.put(TOKTYPE, tokenIS);
        // xmiData.put(POSTYPE, posIS);
        return xmiData;
    }

    @Test
    public void testSplitDocument() throws Exception {
        ArrayList<String> elementsToStore = Lists.newArrayList(TAD, TDC, TMM, TS, TT, TDR, TPOS, TCNP);
        boolean recursively = true;
        boolean storeBaseDocument = true;
        String docTableName = DOC;
        StaxXmiSplitter splitter = new StaxXmiSplitter(elementsToStore, recursively, storeBaseDocument, docTableName, BASE_DOCUMENT_ANNOTATIONS);

        String path = SRCTR + "splitTestDocs/22087118/";
        byte[] ba = FileUtils.readFileToByteArray(new File(path + "22087118.xmi"));
        JCas aCas = getTestCAS().getJCas();
        int nextPossibleId = 0;
        XmiSplitterResult result = splitter.process(ba, aCas, nextPossibleId, defaultSofaIdMap);

        HashMap<String, ByteArrayOutputStream> xmiData = (HashMap<String, ByteArrayOutputStream>) result.xmiData;
        Integer newXmiId = (Integer) result.maxXmiId;
        System.out.println(newXmiId);
        for (String tableName : xmiData.keySet()) {
            byte[] data = xmiData.get(tableName).toByteArray();
            System.out.println(tableName + " " + new String(data));
        }
    }

    @Test
    public void testSetOriginalSofaXmiId() throws Exception {
        // we pretend the original sofa xmi ID was something different than 1
        Map<String, Integer> originalSofaIdMap = new HashMap<>();
        originalSofaIdMap.put("_InitialView", 42);
        ArrayList<String> elementsToStore = Lists.newArrayList(TAD, TDC, TMM, TS, TT, TDR, TPOS, TCNP);
        boolean recursively = true;
        boolean storeBaseDocument = true;
        String docTableName = DOC;
        StaxXmiSplitter splitter = new StaxXmiSplitter(elementsToStore, recursively, storeBaseDocument, docTableName, BASE_DOCUMENT_ANNOTATIONS);

        String path = SRCTR + "splitTestDocs/22087118/";
        byte[] ba = FileUtils.readFileToByteArray(new File(path + "22087118.xmi"));
        JCas aCas = getTestCAS().getJCas();
        int nextPossibleId = 0;
        XmiSplitterResult result = splitter.process(ba, aCas, nextPossibleId, originalSofaIdMap);

        HashMap<String, ByteArrayOutputStream> xmiData = (HashMap<String, ByteArrayOutputStream>) result.xmiData;
        Integer newXmiId = (Integer) result.maxXmiId;
        System.out.println(newXmiId);
        for (String tableName : xmiData.keySet()) {
            byte[] data = xmiData.get(tableName).toByteArray();
            String xmiDataString = new String(data);
//			System.out.println(tableName + " " + xmiDataString);
            // check that the sofa reference ID has been set to the value we gave in the map
            assertTrue(xmiDataString.contains("sofa=\"42\""));
        }
    }

    private Map<String, String> readNamespaceMap(String path) throws IOException {
        LineIterator lit = FileUtils.lineIterator(new File(path));
        Map<String, String> nsAndXmiVersionMap = new HashMap<>();
        while (lit.hasNext()) {
            String line = lit.nextLine();
            String[] split = line.split(" ");
            nsAndXmiVersionMap.put(split[0], split[1]);
        }
        return nsAndXmiVersionMap;
    }

    @Test
    public void testHeaderInformation() throws Exception {
        JCas jcas = JCasFactory.createJCas("de.julielab.jcore.types.jcore-all-types");
        jcas.setDocumentText("some dummy text");
        Header h = new Header(jcas);
        h.setDocId("1234");
        AuthorInfo ai = new AuthorInfo(jcas);
        ai.setForeName("Hans");
        ai.setLastName("Meyer");
        FSArray ais = new FSArray(jcas, 1);
        ais.set(0, ai);
        h.setAuthors(ais);
        h.addToIndexes();

        new Token(jcas, 0, 5).addToIndexes();

        StaxXmiSplitter xmiSplitter = new StaxXmiSplitter(Lists.newArrayList(TOKTYPE), true, true, DOC, new HashSet<>(Arrays.asList("de.julielab.jcore.types.pubmed.Header")));
        XmiSplitterResult result = xmiSplitter.process(getByteArrayInputStream(jcas), jcas, 0, Collections.<String, Integer>emptyMap());
        System.out.println(result.xmiData.get(DOC).toString());
        System.out.println(result.xmiData.get(TOKTYPE).toString());
    }

    @Test
    public void testSofaXmiIdGreaterThanOne() throws Exception {
        JCas jCas = JCasFactory.createJCas("de.julielab.jcore.types.jcore-all-types");
        AbstractSectionHeading h = new AbstractSectionHeading(jCas);
        AbstractSection as = new AbstractSection(jCas);
        as.setAbstractSectionHeading(h);
        as.addToIndexes();
        AbstractText at = new AbstractText(jCas);
        FSArray fa = new FSArray(jCas, 2);
        fa.set(0, as);
        fa.set(1, new AbstractSection(jCas));
        at.setStructuredAbstractParts(fa);
        at.addToIndexes();
        jCas.setDocumentText("This text is set after the first annotations have been created and added to CAS indexes.");
        Sentence s = new Sentence(jCas, 0, 12);
        s.addToIndexes();
        new Token(jCas, 0, 10).addToIndexes();
        new Token(jCas, 0, 10).addToIndexes();
        new Token(jCas, 0, 10).addToIndexes();
        new Token(jCas, 0, 10).addToIndexes();
        new Token(jCas, 0, 10).addToIndexes();
        new Token(jCas, 0, 10).addToIndexes();
        new Token(jCas, 0, 10).addToIndexes();
        new Token(jCas, 0, 10).addToIndexes();
        new Token(jCas, 0, 10).addToIndexes();
        new Token(jCas, 0, 10).addToIndexes();

        StaxXmiSplitter xmiSplitter = new StaxXmiSplitter(Lists.newArrayList(SENTTYPE, TOKTYPE), true, true, DOC, BASE_DOCUMENT_ANNOTATIONS);
        byte[] bais = getByteArrayInputStream(jCas);
        XmiSplitterResult result = xmiSplitter.process(bais, jCas, 0, Collections.<String, Integer>emptyMap());
        ByteArrayOutputStream docbaos = result.xmiData.get(DOC);
        ByteArrayOutputStream sentbaos = result.xmiData.get(SENTTYPE);
        ByteArrayOutputStream tokbaos = result.xmiData.get(TOKTYPE);

        LinkedHashMap<String, InputStream> inputmap = new LinkedHashMap<>();
        inputmap.put(DOC, new ByteArrayInputStream(docbaos.toByteArray()));
        inputmap.put(SENTTYPE, new ByteArrayInputStream(sentbaos.toByteArray()));
        inputmap.put(TOKTYPE, new ByteArrayInputStream(tokbaos.toByteArray()));
        XmiBuilder xmiBuilder = new XmiBuilder(result.namespaces, new String[]{SENTTYPE, TOKTYPE});
        ByteArrayOutputStream builtXmi = xmiBuilder.buildXmi(inputmap, DOC, jCas.getTypeSystem());

        int maxXmiId = xmiSplitter.determineMaxXmiId(builtXmi.toByteArray());
        assertEquals(18, maxXmiId);

        // now lets check, if we a) can deserialize the data again and if b) the
        // access to annotations works. Sometimes, errors only appear when
        // trying to access data (e.g. when xmi:id references point to the wrong
        // element; this will deserialize properly, but on access a
        // ClassCastException will arise).
        jCas.reset();
        System.out.println(new String(builtXmi.toByteArray()));
        XmiCasDeserializer.deserialize(new ByteArrayInputStream(builtXmi.toByteArray()), jCas.getCas());
        FSIterator<Annotation> it = jCas.getAnnotationIndex(AbstractText.type).iterator();
        at = (AbstractText) it.next();
        AbstractSection section = at.getStructuredAbstractParts(0);
        // the system out provokes an exception if the Sofa XMI ID has been
        // assigned to another annotation, too; i.e. we don't want to see an
        // exception here, but it happened originally, before fixing the
        // respective bug. Now it's still here to check the bug stays fixed.
        System.out.println(section);
    }


    @Test
    public void testSharedArray() throws UIMAException, IOException, SAXException, XMISplitterException {
        // In this test we check that feature structures that have been referenced by annotations of two different types
        // recursively are
        // 1. Stored with each annotation type
        // 2. Are unified in the case that both annotation types are used to build a new XMI document (de-duplication)
        JCas jCas = JCasFactory.createJCas("de.julielab.jcore.types.jcore-all-types", "otherTokenTypeDescriptor");
        jCas.setDocumentText("token1 token2");
        Token t1 = new Token(jCas, 0, 6);
        OtherToken t2 = new OtherToken(jCas, 7, 13);
        PennBioIEPOSTag pos = new PennBioIEPOSTag(jCas, 0, 0);
        FSArray fsArray = new FSArray(jCas, 1);
        fsArray.set(0, pos);
        t1.setPosTag(fsArray);
        t2.setPosTag(fsArray);
        t1.addToIndexes();
        t2.addToIndexes();

        StaxXmiSplitter xmiSplitter = new StaxXmiSplitter(
                Arrays.asList(Token.class.getCanonicalName(), OtherToken.class.getCanonicalName()),
                true,
                true,
                "documents",
                new HashSet<>());
        XmiSplitterResult result = xmiSplitter.process(getByteArrayInputStream(jCas), jCas, 0, new HashMap<>());
        ByteArrayOutputStream tokenBaos = result.xmiData.get(Token.class.getCanonicalName());
        ByteArrayOutputStream otherTokenBaos = result.xmiData.get(OtherToken.class.getCanonicalName());

        // Now build a new XMI document only with the Token data
        XmiBuilder xmiBuilder = new XmiBuilder(result.namespaces, new String[]{Token.class.getCanonicalName()});
        LinkedHashMap<String, InputStream> annotations = new LinkedHashMap<>();
        annotations.put(Token.class.getCanonicalName(), new ByteArrayInputStream(tokenBaos.toByteArray()));
        annotations.put("documents", new ByteArrayInputStream(result.xmiData.get("documents").toByteArray()));
        ByteArrayOutputStream builtXmi = xmiBuilder.buildXmi(annotations, "documents", jCas.getTypeSystem());
        jCas.reset();
        // Deserialize the built XMI and check that there is exactly one POS tag
        XmiCasDeserializer.deserialize(new ByteArrayInputStream(builtXmi.toByteArray()), jCas.getCas());
        Token token = JCasUtil.selectSingle(jCas, Token.class);
        assertNotNull(token.getPosTag());
        assertEquals(1, token.getPosTag().size());

        // Now do the same with the OtherToken data. We still expect the same POS tag
        xmiBuilder = new XmiBuilder(result.namespaces, new String[]{OtherToken.class.getCanonicalName()});
        annotations = new LinkedHashMap<>();
        annotations.put(OtherToken.class.getCanonicalName(), new ByteArrayInputStream(otherTokenBaos.toByteArray()));
        annotations.put("documents", new ByteArrayInputStream(result.xmiData.get("documents").toByteArray()));
        builtXmi = xmiBuilder.buildXmi(annotations, "documents", jCas.getTypeSystem());
        jCas.reset();
        XmiCasDeserializer.deserialize(new ByteArrayInputStream(builtXmi.toByteArray()), jCas.getCas());
        OtherToken otherToken = JCasUtil.selectSingle(jCas, OtherToken.class);
        assertNotNull(otherToken.getPosTag());
        assertEquals(1, otherToken.getPosTag().size());
    }

    private byte[] getByteArrayInputStream(JCas jCas) throws SAXException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmiCasSerializer.serialize(jCas.getCas(), baos);

        return baos.toByteArray();
    }

}