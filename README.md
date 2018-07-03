## Simplified Fuzzy Phrase Matching Library 
Powered by second String Project(SoftTFIDF)


#### Example

```java
package com.github.kayr.phrasematcher;

import java.util.Arrays;
import java.util.List;
import com.github.kayr.phrasematcher.PhraseMatcher;

public class XX {
   
    public static void main(String[] args) {
        List<String> strings = Arrays.asList(
                "This is a dog",
                "Dog this is",
                "This is a cat",
                "Some other phrase",
                "And another phrase"
        );

        PhraseMatcher matcher = PhraseMatcher.train(strings);


        //Hits above 50%
        PhraseMatcher.MatchResult result = matcher.bestHit("Ths is a dig", 0.5);
        System.out.println(result);
        //Output 
        //MatchResult{phrase='This is a dog', score=0.5023550489416228}


        //Get hits about 35%
        List<PhraseMatcher.MatchResult> results = matcher.bestHitList("Ths is a dig", 0.35);
        System.out.println(results);
        //Output
        //[MatchResult{phrase='This is a dog', score=0.5023550489416228}, MatchResult{phrase='Dog this is', score=0.35163044073961364}, MatchResult{phrase='This is a cat', score=0.374913839423641}]

    }
}

```
