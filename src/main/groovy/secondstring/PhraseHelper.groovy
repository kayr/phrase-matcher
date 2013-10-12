package secondstring

import com.wcohen.ss.BasicStringWrapperIterator
import com.wcohen.ss.JaroWinkler
import com.wcohen.ss.SoftTFIDF
import com.wcohen.ss.api.Tokenizer
import com.wcohen.ss.tokens.SimpleTokenizer

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 12/28/12
 * Time: 8:44 PM
 * To change this template use File | Settings | File Templates.
 */
class PhraseHelper {

    SoftTFIDF distance
    List<String> phrases

   static PhraseHelper train(List<String> words) {
        // create a SoftTFIDF distance learner
        Tokenizer tokenizer = new SimpleTokenizer(false, true);
        double minTokenSimilarity = 0.8;
        SoftTFIDF distance = new SoftTFIDF(tokenizer, new JaroWinkler(), 0.8);

        // train the distance on some strings - in general, this would
        // be a large corpus of existing strings, so that some
        // meaningful frequency estimates can be accumulated.  for
        // efficiency, you train on an iterator over StringWrapper
        // objects, which are produced with the 'prepare' function.

        def list = words.collect {
            distance.prepare(it)
        }
        distance.train(new BasicStringWrapperIterator(list.iterator()));

        return new PhraseHelper(distance: distance, phrases: words)
    }

    float compare(String s1, String s2) {
        return distance.score(s1, s2) * 100;
    }

    String bestHitFromList(String s1, List<String> phrases, float best = 70){
             return bestInternalHit(s1,best,phrases)
    }

    String bestInternalHit(String s1, float  best = 70,List<String> phrases = this.phrases) {
        boolean anyAboveBest = false
        def rt = phrases.max {  phrase ->
            def f = compare(s1, phrase)
            if(f >= best)
                anyAboveBest = true
            return (f == Float.NaN || f < best)? -1 : f
        }
        return anyAboveBest? rt : null

    }

    Float bestScore(String s1, List<String> phrases, float best = 70){
        return bestInternalScore(s1,best,phrases)
    }

    Float bestInternalScore(String s1, float  best = 70,List<String> phrases = this.phrases) {
        def scores = phrases.collect {  phrase ->
            return compare(s1, phrase)
        }
        return scores.max()
    }

    List<String> internalHits(String s1, float best = 70, List<String> phrases = this.phrases) {
        return phrases.findAll {
            def f = compare(s1, it)
            (f >= best && f != Float.NaN)
        }
    }

    List<String> hitFromList(String s1,  List<String> phrases,float best = 70 ) {
        return internalHits(s1,best,phrases)
    }


}
