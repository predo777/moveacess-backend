package br.com.moveacess.util;

public final class GeoUtils {
    private static final double EARTH_RADIUS_M = 6_371_000;
    private GeoUtils() {}

    /** Distância em metros (fórmula de Haversine). */
    public static double distanceMeters(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return EARTH_RADIUS_M * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public static double metersToLatDegrees(double meters) { return meters / 111_320.0; }

    public static double metersToLngDegrees(double meters, double lat) {
        return meters / (111_320.0 * Math.cos(Math.toRadians(lat)));
    }
}
