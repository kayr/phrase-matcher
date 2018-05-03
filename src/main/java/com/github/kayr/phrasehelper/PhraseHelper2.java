package com.github.kayr.phrasehelper;

import com.wcohen.ss.BasicStringWrapper;
import com.wcohen.ss.BasicStringWrapperIterator;
import com.wcohen.ss.JaroWinkler;
import com.wcohen.ss.SoftTFIDF;
import com.wcohen.ss.api.StringWrapper;
import com.wcohen.ss.tokens.SimpleTokenizer;

import java.util.ArrayList;
import java.util.List;

public class PhraseHelper2 {

    public static final double DEFAULT_MIN_SCORE = 0.7;
    SoftTFIDF    distance;
    List<String> phrases;

    private SoftTFIDF getDistance() {
        return distance;
    }

    private PhraseHelper2 setDistance(SoftTFIDF distance) {
        this.distance = distance;
        return this;
    }

    public List<String> getPhrases() {
        return phrases;
    }

    private PhraseHelper2 setPhrases(List<String> phrases) {
        this.phrases = phrases;
        return this;
    }

    public static PhraseHelper2 train(List<String> words) {

        // create a SoftTFIDF distance learner
        SimpleTokenizer tokenizer = new SimpleTokenizer(false, true);
        SoftTFIDF       distance  = new SoftTFIDF(tokenizer, new JaroWinkler(), 0.8d);

        // train the distance on some strings - in general, this would
        // be a large corpus of existing strings, so that some
        // meaningful frequency estimates can be accumulated.  for
        // efficiency, you train on an iterator over StringWrapper
        // objects, which are produced with the 'prepare' function.

        List<StringWrapper> wrappers = new ArrayList<StringWrapper>(words.size());

        for (String word : words) {
            wrappers.add(new BasicStringWrapper(word));
        }


        distance.prepare(new BasicStringWrapperIterator(wrappers.iterator()));
        distance.train(new BasicStringWrapperIterator(wrappers.iterator()));

        return new PhraseHelper2().setDistance(distance).setPhrases(words);
    }

    public double compare(String s1, String s2) {
        return distance.score(s1, s2);
    }

    public MatchResult bestHit(String s1, List<String> phrases) {
        return bestHit(s1, DEFAULT_MIN_SCORE, phrases);
    }

    public MatchResult bestHit(String s1) {
        return bestHit(s1, DEFAULT_MIN_SCORE, phrases);
    }

    public MatchResult bestHit(String s1, double best) {
        return bestHit(s1, best, phrases);
    }

    public MatchResult bestHit(String s1, double minimumScore, List<String> phrases) {
        double highestScore    = -1;
        String highScorePhrase = null;
        for (String phrase : phrases) {
            double score = compare(s1, phrase);
            if (score >= minimumScore && score >= highestScore) {
                highestScore = score;
                highScorePhrase = phrase;
            }
        }

        return new MatchResult().setPhrase(highScorePhrase).setScore(highestScore);
    }


    public List<MatchResult> bestHitList(String s1) {
        return bestHitList(s1, DEFAULT_MIN_SCORE);
    }

    public List<MatchResult> bestHitList(String s1, double best) {
        return bestHitList(s1, best, phrases);
    }

    public List<MatchResult> bestHitList(String s1, double minScore, List<String> phrases) {
        List<MatchResult> results = new ArrayList<MatchResult>();
        for (String phrase : phrases) {
            double score = compare(s1, phrase);
            if (score >= minScore) {
                results.add(new MatchResult().setPhrase(phrase).setScore(score));
            }
        }
        return results;
    }


    public static class MatchResult {
        private String phrase;
        private double score;

        public String getPhrase() {
            return phrase;
        }

        public MatchResult setPhrase(String phrase) {
            this.phrase = phrase;
            return this;
        }

        public double getScore() {
            return score;
        }

        public MatchResult setScore(double score) {
            this.score = score;
            return this;
        }

        public boolean isValid() {
            return !isInvalid();
        }

        public boolean isInvalid() {
            return score == -1;
        }

        @Override
        public String toString() {
            return "MatchResult{" +
                    "phrase='" + phrase + '\'' +
                    ", score=" + score +
                    '}';
        }
    }

}
