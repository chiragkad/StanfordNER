import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NERClassifier {

    public static void main(String[] args) throws IOException {
        String s1 = "Angelika arbeitet bei BMW am 02/04/2003 ins London.";
        Classifier classifier = new Classifier();

        ArrayList<NERNode> nodes = classifier.classify(s1);
    }
}

class NERNode{
    String type;
    String word;
}

class Classifier{

    ArrayList<String> tags = new ArrayList( Arrays.asList
            (new String[]{"I-PER", "I-LOC","B-LOC","I-ORG","B-ORG"}));
    String serializedClassifier = "edu/stanford/nlp/models/ner/german.conll.hgc_175m_600.crf.ser.gz";
    AbstractSequenceClassifier classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
    String punctutations = ".,:;?";

    public String clean(String str) {
        String cleanedString = "";
        for(int i=0;i<str.length();i++) {
            if(!punctutations.contains(Character.toString(str.charAt(i)))) {
                cleanedString = cleanedString.concat(Character.toString(str.charAt(i)));
            }
        }
        return cleanedString;
    }

    public ArrayList<NERNode> classify(String string) {
        ArrayList<NERNode> nerNodes = new ArrayList<>();
        string = clean(string);
        String tok[] = string.split(" ");
        Pattern p = Pattern.compile("^([0-2][0-9]||3[0-1])/(0[0-9]||1[0-2])/([0-9][0-9])?[0-9][0-9]$");
        for(String token : tok) {
            Matcher m = p.matcher(token);
            while (m.find()) {
                NERNode nerNode = new NERNode();
                nerNode.word = m.group();
                nerNode.type = "DAT";
                nerNodes.add(nerNode);
            }
        }
        String classifyString = classifier.classifyToString(string);
        String tokens[] = classifyString.split(" ");
        for (int i=0;i<tokens.length;i++) {
            String words[]=tokens[i].split("/");
            if(tags.contains(words[1])) {
                NERNode nerNode = new NERNode();
                String label[] =  words[1].split("-");
                nerNode.type = label[1];
                nerNode.word = words[0];
                nerNodes.add(nerNode);
            }
        }
        return nerNodes;
    }
}