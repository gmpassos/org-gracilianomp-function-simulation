package org.gracilianomp.utils;

public class Statistics {

    static public double mean(int[] ns) {
        double total = sum(ns);
        double mean = total / ns.length ;
        return mean ;
    }

    static public long sum(int[] ns) {
        long total = 0 ;

        for (int n : ns) {
            total += n ;
        }

        return total ;
    }


    static public double mean(double[] vals) {
        return mean(vals, vals.length);
    }

    static public double mean(double[] vals, int sz) {
        double mean = 0;

        for (int i = sz-1 ; i >= 0 ; i--) {
            mean += vals[i];
        }

        mean /= sz;

        return mean;
    }

    static public float mean(float[] vals) {
        return mean(vals, vals.length);
    }

    static public float mean(float[] vals, int sz) {
        double mean = 0;

        for (int i = sz-1 ; i >= 0 ; i--) {
            mean += vals[i];
        }

        mean /= sz;

        return (float) mean;
    }

    static public double standardDeviation(double[] vals) {
        return standardDeviation(vals, mean(vals));
    }

    static public double standardDeviation(double[] vals, double mean) {
        if (vals.length == 1) return 0;

        double sum = 0;

        for (int i = vals.length - 1 ; i >= 0 ; i--) {
            double v = vals[i] - mean;
            v = v * v;
            sum += v;
        }

        double variance = sum / vals.length;

        double deviation = Math.sqrt(variance);

        return deviation;
    }

    static public float standardDeviation(float[] vals) {
        return standardDeviation(vals, vals.length);
    }

    static public float standardDeviation(float[] vals, int sz) {
        return standardDeviation(vals, mean(vals, sz));
    }

    static public float standardDeviation(float[] vals, float mean) {
        return standardDeviation(vals, mean, vals.length);
    }

    static public float standardDeviation(float[] vals, float mean, int sz) {
        if (sz <= 1) return 0;

        double sum = 0;

        for (int i = sz-1 ; i >= 0 ; i--) {
            double v = vals[i] - mean;
            v = v * v;
            sum += v;
        }

        double variance = sum / sz;

        double deviation = Math.sqrt(variance);

        return (float) deviation;
    }

    static public double[] standardDeviationUpperLower(double[] vals, double mean) {
        if (vals.length == 1) return new double[] {0, 0};

        double sumUpper = 0;
        double sumLower = 0;

        int upperSamples = 0;
        int lowerSamples = 0;

        for (int i = vals.length - 1 ; i >= 0 ; i--) {
            double v = vals[i] - mean;

            double vv = v * v;

            if (v == 0) {
                upperSamples++;
                lowerSamples++;

                sumUpper += vv;
                sumLower += vv;
            }
            else if (v > 0) {
                upperSamples++;
                sumUpper += vv;
            }
            else {
                lowerSamples++;
                sumLower += vv;
            }
        }

        double varianceUpper = sumUpper / upperSamples;
        double varianceLower = sumLower / lowerSamples;

        double deviationUpper = Math.sqrt(varianceUpper);
        double deviationLower = Math.sqrt(varianceLower);

        return new double[] {deviationUpper, deviationLower};
    }

    static public double meanAbsoluteDeviation(double[] vals) {
        return meanAbsoluteDeviation(vals, mean(vals));
    }

    static public double meanAbsoluteDeviation(double[] vals, double mean) {
        if (vals.length == 1) return 0;

        double sum = 0;

        for (int i = vals.length - 1 ; i >= 0 ; i--) {
            double v = vals[i] - mean;
            if (v < 0) v = -v;
            sum += v;
        }

        double deviation = sum / vals.length;

        return deviation;
    }

    static public int max(int[] vals) {
        int max = vals[0] ;

        for (int i = 0 ; i < vals.length ; i++) {
            int v = vals[i];
            if (v > max) max = v;
        }

        return max;
    }


    static public double max(double[] vals) {
        double max = Double.NEGATIVE_INFINITY;

        for (int i = vals.length - 1 ; i >= 0 ; i--) {
            double v = vals[i];

            if (v > max) max = v;
        }

        return max;
    }

    static public float max(float[] vals) {
        float max = Float.NEGATIVE_INFINITY;

        for (int i = vals.length - 1 ; i >= 0 ; i--) {
            float v = vals[i];

            if (v > max) max = v;
        }

        return max;
    }

    static public int min(int[] vals) {
        int min = vals[0] ;

        for (int i = 1 ; i < vals.length ; i++) {
            int v = vals[i];
            if (v < min) min = v;
        }

        return min;
    }

    static public double min(double[] vals) {
        double min = Double.POSITIVE_INFINITY;

        for (int i = vals.length - 1 ; i >= 0 ; i--) {
            double v = vals[i];

            if (v < min) min = v;
        }

        return min;
    }

    static public float min(float[] vals) {
        float min = Float.POSITIVE_INFINITY;

        for (int i = vals.length - 1 ; i >= 0 ; i--) {
            float v = vals[i];

            if (v < min) min = v;
        }

        return min;
    }

    static public int[] minMax(int[] vals) {
        int min , max ;

        max = min = vals[0];

        for (int i = 1 ; i < vals.length ; i++) {
            int v = vals[i];

            if (v < min) min = v;
            if (v > max) max = v;
        }

        return new int[] {min, max};
    }

    static public double[] minMax(double[] vals) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        for (int i = vals.length - 1 ; i >= 0 ; i--) {
            double v = vals[i];

            if (v < min) min = v;
            if (v > max) max = v;
        }

        return new double[] {min, max};
    }

    static public float[] minMax(float[] vals) {
        float min = Float.POSITIVE_INFINITY;
        float max = Float.NEGATIVE_INFINITY;

        for (int i = vals.length - 1 ; i >= 0 ; i--) {
            float v = vals[i];

            if (v < min) min = v;
            if (v > max) max = v;
        }

        return new float[] {min, max};
    }

    static public double[] normalize(double[] vals) {
        double[] minMax = minMax(vals);
        return normalize(vals, minMax[0], minMax[1]);
    }

    static public double[] normalize(double[] vals, double min, double max) {
        double scale = max - min;

        double[] norm = new double[vals.length];

        for (int i = norm.length - 1 ; i >= 0 ; i--) {
            norm[i] = (vals[i] - min) / scale;
        }

        return norm;
    }

    static public float[] normalize(float[] vals) {
        float[] minMax = minMax(vals);
        return normalize(vals, minMax[0], minMax[1]);
    }

    static public float[] normalize(float[] vals, float min, float max) {
        float scale = max - min;

        float[] norm = new float[vals.length];

        for (int i = norm.length - 1 ; i >= 0 ; i--) {
            norm[i] = (vals[i] - min) / scale;
        }

        return norm;
    }

    static public void normalizeInLoco(float[] vals, float min, float max) {
        float scale = max - min;

        for (int i = vals.length - 1 ; i >= 0 ; i--) {
            vals[i] = (vals[i] - min) / scale;
        }
    }

    static public void normalizeByDeviationInLoco(float[] vals, float deviationLimitRatio) {
        normalizeByDeviationInLoco(vals, deviationLimitRatio, vals.length) ;
    }

    static public void normalizeByDeviationInLoco(float[] vals, float deviationLimitRatio, int sz) {

        float mean = mean(vals, sz);
        float deviation = standardDeviation(vals, mean, sz) * deviationLimitRatio;

        for (int i = sz-1 ; i >= 0 ; i--) {
            float v = vals[i];

            if (v >= mean) {
                v = 0.5f + ((v - mean) / deviation);
                if (v > 1) v = 1;
            }
            else {
                v = 0.5f - ((mean - v) / deviation);
                if (v < 0) v = 0;
            }

            vals[i] = v;
        }
    }

    static public boolean isInNormalizedRange(float[] vals) {
        for (int i = vals.length - 1 ; i >= 0 ; i--) {
            float v = vals[i];
            if (v < 0 || v > 1) return false;
        }
        return true;
    }

    static public boolean checkInNormalizedRange(float[] vals) {
        if (!isInNormalizedRange(vals)) throw new IllegalArgumentException("All values not in normalized range[0..1]!");
        return true;
    }

    static public float diff(float v1, float v2) {
        float d = v2 - v1;
        if (d < 0) d = -d;
        return d;
    }

    static public double diff(double v1, double v2) {
        double d = v2 - v1;
        if (d < 0) d = -d;
        return d;
    }

    static public float[] diff(float[] a1, float[] a2) {
        float[] d = new float[a1.length];
        for (int i = 0 ; i < d.length ; i++) {
            d[i] = diff(a1[i], a2[i]);
        }
        return d;
    }

    static public double[] diff(double[] a1, double[] a2) {
        double[] d = new double[a1.length];
        for (int i = 0 ; i < d.length ; i++) {
            d[i] = diff(a1[i], a2[i]);
        }
        return d;
    }

    static public float findMaxValue(float v1, float v2, float v3, float v4) {
        if (v1 > v2 && v1 > v3 && v1 > v4) return v1;
        if (v2 > v3 && v2 > v4) return v2;
        if (v3 > v4) return v3;
        return v4;
    }

    static public double sum(double[] values) {
        double sum = 0 ;

        for (int i = values.length-1; i >= 0 ; i--) {
            sum += values[i];
        }

        return sum ;
    }

    static public double[] softmax(double[] values) {
        return softmax(values, values.length) ;
    }

    static public double[] softmax(double[] values, int sz) {
        int szM1 = sz - 1;

        double[] exp = new double[sz] ;

        for (int i = szM1; i >= 0; i--) {
            exp[i] = Math.exp( values[i] );
        }

        double sum = sum(exp) ;

        for (int i = szM1; i >= 0; i--) {
            exp[i] = exp[i] / sum;
        }

        return exp ;
    }

    static public double[] softmax2(double[] values, int sz) {
        int szM1 = sz - 1;

        double[] exp = new double[sz] ;

        for (int i = szM1; i >= 0; i--) {
            double v = values[i];
            exp[i] = v*v;
        }

        double sum = sum(exp) ;

        for (int i = szM1; i >= 0; i--) {
            exp[i] = exp[i] / sum;
        }

        return exp ;
    }

    static public double[] softmaxNormalized(double[] values) {
        return softmaxNormalized(values, values.length);
    }

    static public double[] softmaxNormalized(double[] values, int sz) {
        double[] softmax = softmax(values, sz);

        double min , max ;
        min = max = softmax[sz-1] ;

        for (int i = sz-2; i >= 0; i--) {
            double v = softmax[i];

            if (v < min) min = v ;
            if (v > max) max = v ;
        }

        double scale = max - min ;

        if (scale == 0) {
            for (int i = sz-1; i >= 0; i--) {
                softmax[i] = 0;
            }
            return softmax ;
        }

        for (int i = sz-1; i >= 0; i--) {
            double v = softmax[i];
            double n = (v - min) / scale;
            softmax[i] = n ;
        }

        return softmax ;
    }

    static public double[] softmaxFast(double[] values) {
        return softmaxFast(values, values.length) ;
    }

    static public double[] softmaxFast(double[] values, int sz) {
        int szM1 = sz - 1;

        double[] exp = new double[sz] ;

        double sum = 0 ;

        for (int i = szM1; i >= 0; i--) {
            double e = Math.exp(values[i]);
            exp[i] = e;
            sum += e ;
        }

        for (int i = szM1; i >= 0; i--) {
            exp[i] = exp[i] / sum;
        }

        return exp ;
    }

    static public double[] softmaxFastNormalized(double[] values) {
        return softmaxFastNormalized(values, values.length);
    }

    static public double[] softmaxFastNormalized(double[] values, int sz) {
        double[] softmax = softmaxFast(values, sz);

        double min , max ;
        min = max = softmax[sz-1] ;

        for (int i = sz-2; i >= 0; i--) {
            double v = softmax[i];

            if (v < min) min = v ;
            if (v > max) max = v ;
        }

        double scale = max - min ;

        if (scale == 0) {
            for (int i = sz-1; i >= 0; i--) {
                softmax[i] = 0;
            }
            return softmax ;
        }

        for (int i = sz-1; i >= 0; i--) {
            double v = softmax[i];
            double n = (v - min) / scale;
            softmax[i] = n ;
        }

        return softmax ;
    }

    static public void normalize(double[] values, int sz) {
        double min , max ;
        min = max = values[sz-1] ;

        for (int i = sz-2; i >= 0; i--) {
            double v = values[i];

            if (v < min) min = v ;
            if (v > max) max = v ;
        }

        double scale = max - min ;

        if (scale == 0) {
            for (int i = sz-1; i >= 0; i--) {
                values[i] = 0;
            }
        }
        else {
            for (int i = sz-1; i >= 0; i--) {
                double v = values[i];
                double n = (v - min) / scale;
                values[i] = n ;
            }
        }

    }

}
