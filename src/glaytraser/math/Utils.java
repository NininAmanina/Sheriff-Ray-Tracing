package glaytraser.math;

public class Utils {
    public static final double EPSILON = 0.0000000001;
    public static final double REFLECT_EPSILON = 0.001;

//    public static double getBestRoot(final double [] adC) {
//        // http://en.wikipedia.org/wiki/Quartic_function
//        final double B = adC[0];
//        final double C = adC[1];
//        final double D = adC[2];
//        final double E = adC[3];
//        final double alpha = -0.375 * B * B + C;
//        final double beta = 0.125 * B * B * B - 0.5 * B * C + D;
//        final double gamma = -3 * 0.0625 * B * B * 0.0625 * B * B + 0.0625 * C * B * B - 0.25 * B * D + E;
//        if(Math.abs(beta) < EPSILON) {
//            // Simple case
//        } else {
//            // More complicated case
//            final double P = -alpha * alpha - gamma;
//            final double Q = alpha * (-alpha * alpha * 0.25 / 9.0 + gamma) / 3.0 - 0.125 * beta * beta;
//            final double R = -0.5 * Q 
//        }
//    }
//    public static double getBestRoot(final double [] adC) {
//        // Assume x^4 + adC[0] * x^3 + adC[1] * x^2 + adC[2] * x + adC[3] = 0
//        // Applying http://en.wikipedia.org/wiki/Quartic_function
//        double B = adC[0];
//        double C = adC[1];
//        double D = adC[2];
//        double E = adC[3];
//        double B_2 = B * B;
//        double B_OVER_4 = 0.25 * B;
//        // Reparameterise:  x = u - 0.25 * B
//        // u^4 + alpha * u^2 + beta * u + gamma = 0
//        double alpha = -3 * B_2 + C;
//        double beta = 0.5 * B * (0.25 * B_2 - C) + D;
//        double gamma = B_OVER_4 * (-D + B_OVER_4 * (C - 3 * B_2 * 0.0625));
//        if(Math.abs(beta) < EPSILON) {
//            // Biquadratic equation -- can get quadratic roots and then square roots of those
//            double [] tArray = quadraticRoot(1, alpha, gamma);
//            if(tArray == null) {
//                return 0;
//            }
//            return Math.min(Math.max(0,  tArray[0] - B_OVER_4), Math.max(0,  tArray[1] - B_OVER_4));
//        } else if(Math.abs(gamma) < EPSILON) {
//            // TODO:  u = 0 is one root.  Solve the depressed cubic equation u^3 + alpha * u + beta = 0
//            return getBestCubicRoot(alpha, beta) - B_OVER_4;
//        } else {
//            // TODO:  Solve the depressed quartic
//        }
//        return 0;
//    }

    private static double getBestCubicRoot(final double alpha, final double beta) {
        // TODO:  u = 0 is one root.  Solve the depressed cubic equation u^3 + alpha * u + beta = 0
        // Degenerate case
        if(Math.abs(alpha) < EPSILON) {
            return Math.max(0, Math.cbrt(-beta));
        } else if(Math.abs(beta) < EPSILON) {
            if(Math.abs(alpha) > 0) {
                return 0;
            } else {
                return Math.abs(Math.sqrt(alpha));
            }
        }
        final double discriminant = 0.25 * beta * beta + 0.037037037 * alpha * alpha;
        if(discriminant > EPSILON) {
            // TODO:  Only one real root (http://www.encyclopediaofmath.org/index.php/Cardano_formula)
            final double betaOver2 = 0.5 * beta;
            final double sqrtDisc = Math.sqrt(discriminant);
            return Math.max(0, Math.cbrt(-betaOver2 + sqrtDisc) + Math.cbrt(-betaOver2 - sqrtDisc));
        } else if(discriminant > -EPSILON) {
            // One single real root, one duplicated real root
            return Math.min(Math.max(0, -1.5 * beta / alpha), Math.max(0, 3 * beta / alpha));
        } else {
            // TODO:  Three real roots
            double [] tArray = new double [3];
            final double betaOver2 = 0.5 * beta;
            final double sqrtDisc = Math.sqrt(discriminant);
            tArray[0] = Math.cbrt(-betaOver2 + sqrtDisc) + Math.cbrt(-betaOver2 - sqrtDisc);
            return 0;
        }
    }

    // Returns null if there are no real roots, otherwise it returns the real roots
    public static double [] quadraticRoot(double a, double b, double c) {
        double discriminant = b * b - a * c;// * 4;
        if(discriminant < -Utils.EPSILON) {
            return null;
        }
        // If the discriminant's magnitude is less than EPSILON, then make it 0.
        // Otherwise, take its square root and cache the value.
        double sqrtDisc;
        if(discriminant < Utils.EPSILON) {
            sqrtDisc = 0;
        } else {
            sqrtDisc = Math.sqrt(discriminant);
        }
        // TODO:  Make the math below a bit more numerically stable.
        return new double [] { (-b - sqrtDisc) / a, // * 0.5;
                               (-b + sqrtDisc) / a }; // * 0.5;
    }
}
