package com.lukas.app.analyzers;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;

import java.io.IOException;
import java.util.List;

public class CustomAnalyzer extends Analyzer {

    private final CharArraySet stopWords;

    public CustomAnalyzer() {
        CharArraySet customStopWords = new CharArraySet(
                List.of("a", "a's", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "ain't", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "aren't", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "b", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "c", "c'mon", "c's", "came", "can", "can't", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldn't", "course", "currently", "d", "definitely", "described", "despite", "did", "didn't", "different", "do", "does", "doesn't", "doing", "don't", "done", "down", "downwards", "during", "e", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "f", "far", "few", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "g", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "h", "had", "hadn't", "happens", "hardly", "has", "hasn't", "have", "haven't", "having", "he", "he's", "hello", "help", "hence", "her", "here", "here's", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "i'd", "i'll", "i'm", "i've", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isn't", "it", "it'd", "it'll", "it's", "its", "itself", "j", "just", "k", "keep", "keeps", "kept", "know", "knows", "known", "l", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "let's", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "m", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "n", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "o", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "p", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "q", "que", "quite", "qv", "r", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "s", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldn't", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "t", "t's", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "that's", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "there's", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "they'd", "they'll", "they're", "they've", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "u", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "uucp", "v", "value", "various", "very", "via", "viz", "vs", "w", "want", "wants", "was", "wasn't", "way", "we", "we'd", "we'll", "we're", "we've", "welcome", "well", "went", "were", "weren't", "what", "what's", "whatever", "when", "whence", "whenever", "where", "where's", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "who's", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "won't", "wonder", "would", "would", "wouldn't", "x", "y", "yes", "yet", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves", "z", "zero"),
                true
        );
        customStopWords.addAll(EnglishAnalyzer.getDefaultStopSet());
        this.stopWords = customStopWords;
    }

    @Override
    protected TokenStreamComponents createComponents(String ignore) {
        Tokenizer tokenizer = new StandardTokenizer();
        TokenStream tokenStream = new LowerCaseFilter(tokenizer);
        StopFilter stopFilter = new StopFilter(tokenStream, stopWords);
        SynonymGraphFilter synonymGraphFilter = new SynonymGraphFilter(stopFilter, buildSynonymMap(), true);
        KStemFilter kStemFilter = new KStemFilter(synonymGraphFilter);
        return new TokenStreamComponents(tokenizer, kStemFilter);
    }

    private SynonymMap buildSynonymMap() {
        SynonymMap.Builder builder = new SynonymMap.Builder(true);

        builder.add(new CharsRef("aeroplane"), new CharsRef("aircraft"), true);
        builder.add(new CharsRef("airplane"), new CharsRef("aircraft"), true);
        builder.add(new CharsRef("plane"), new CharsRef("aircraft"), true);
        builder.add(new CharsRef("velocity"), new CharsRef("speed"), true);
        builder.add(new CharsRef("rate"), new CharsRef("speed"), true);
        builder.add(new CharsRef("airflow"), new CharsRef("flow"), true);
        builder.add(new CharsRef("air flow"), new CharsRef("flow"), true);
        builder.add(new CharsRef("flowfield"), new CharsRef("flow"), true);
        builder.add(new CharsRef("pressure"), new CharsRef("force"), true);
        builder.add(new CharsRef("boundary layer"), new CharsRef("boundarylayer"), true);
        builder.add(new CharsRef("laminar flow"), new CharsRef("laminarflow"), true);
        builder.add(new CharsRef("turbulent flow"), new CharsRef("turbulentflow"), true);
        builder.add(new CharsRef("aerodynamic"), new CharsRef("aero"), true);
        builder.add(new CharsRef("aerodynamics"), new CharsRef("aero"), true);
        builder.add(new CharsRef("airfoil"), new CharsRef("wing"), true);
        builder.add(new CharsRef("aerofoil"), new CharsRef("wing"), true);
        builder.add(new CharsRef("surface"), new CharsRef("body"), true);
        builder.add(new CharsRef("resistance"), new CharsRef("drag"), true);
        builder.add(new CharsRef("friction"), new CharsRef("drag"), true);
        builder.add(new CharsRef("upward force"), new CharsRef("lift"), true);
        builder.add(new CharsRef("temperature"), new CharsRef("heat"), true);
        builder.add(new CharsRef("thermal"), new CharsRef("heat"), true);
        builder.add(new CharsRef("heating"), new CharsRef("heat"), true);
        builder.add(new CharsRef("shock wave"), new CharsRef("shockwave"), true);
        builder.add(new CharsRef("shock"), new CharsRef("shockwave"), true);
        builder.add(new CharsRef("supersonic"), new CharsRef("highspeed"), true);
        builder.add(new CharsRef("hypersonic"), new CharsRef("highspeed"), true);
        builder.add(new CharsRef("high speed"), new CharsRef("highspeed"), true);
        builder.add(new CharsRef("mach number"), new CharsRef("mach"), true);
        builder.add(new CharsRef("mach no"), new CharsRef("mach"), true);
        builder.add(new CharsRef("wind tunnel"), new CharsRef("windtunnel"), true);
        builder.add(new CharsRef("stability"), new CharsRef("control"), true);
        builder.add(new CharsRef("stabilization"), new CharsRef("control"), true);
        builder.add(new CharsRef("compressible"), new CharsRef("compression"), true);
        builder.add(new CharsRef("compressibility"), new CharsRef("compression"), true);
        builder.add(new CharsRef("viscous"), new CharsRef("viscosity"), true);
        builder.add(new CharsRef("viscid"), new CharsRef("viscosity"), true);
        builder.add(new CharsRef("thrust"), new CharsRef("propulsion"), true);
        builder.add(new CharsRef("engine"), new CharsRef("propulsion"), true);
        builder.add(new CharsRef("jet"), new CharsRef("propulsion"), true);
        builder.add(new CharsRef("experimental"), new CharsRef("test"), true);
        builder.add(new CharsRef("experiment"), new CharsRef("test"), true);
        builder.add(new CharsRef("theoretical"), new CharsRef("theory"), true);
        builder.add(new CharsRef("calculation"), new CharsRef("computation"), true);
        builder.add(new CharsRef("calculate"), new CharsRef("compute"), true);
        builder.add(new CharsRef("numerical"), new CharsRef("computation"), true);
        builder.add(new CharsRef("configuration"), new CharsRef("design"), true);
        builder.add(new CharsRef("geometry"), new CharsRef("design"), true);
        builder.add(new CharsRef("reynolds number"), new CharsRef("reynolds"), true);
        builder.add(new CharsRef("reynolds no"), new CharsRef("reynolds"), true);
        builder.add(new CharsRef("oscillation"), new CharsRef("vibration"), true);
        builder.add(new CharsRef("oscillate"), new CharsRef("vibrate"), true);
        builder.add(new CharsRef("flutter"), new CharsRef("vibration"), true);
        builder.add(new CharsRef("transonic"), new CharsRef("transsonic"), true);
        builder.add(new CharsRef("subsonic"), new CharsRef("lowspeed"), true);
        builder.add(new CharsRef("low speed"), new CharsRef("lowspeed"), true);

        try {
            return builder.build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
