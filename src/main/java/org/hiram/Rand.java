package org.hiram;

import java.util.Random;

public class Rand
{
    // fields
    private static Random rand;

    // internal helper methods
    private static void Initialize()
    {
        if (rand == null)
        {
            rand = new Random();
        }
    }

    // methods
    public static float random()
    {
        Initialize();

        return rand.nextFloat();
    }

    public static int randomInt(int minInclusive, int maxExclusive)
    {
        Initialize();

        if (maxExclusive - minInclusive <= 0)
        {
            System.out.println("Warning: maxExclusive must be greater than minInclusive in Rand.randomInt(min, max).");
            return 0;
        }

        return rand.nextInt(maxExclusive - minInclusive) + minInclusive;
    }

//    public static float randomFloat(float minInclusive, float maxExclusive)
//    {
//        Initialize();
//
//        if (maxExclusive - minInclusive <= 0)
//        {
//            System.out.println("Warning: maxExclusive must be greater than minInclusive in Rand.randomFloat(min, max).");
//            return 0;
//        }
//
//        return rand.nextFloat(maxExclusive - minInclusive) + minInclusive;
//    }
}
