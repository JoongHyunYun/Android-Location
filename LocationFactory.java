package unikys.icu.util;

import unikys.icu.activity.NearMapFragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationFactory {

	private static LocationListener sLocationListener = null;
	private static LocationManager sLocationManager = null;
	
	private static Location sLocation = null;
	private static boolean sIsMeasuringLocation = false;
	
	public static void startLocationMeasure(final NearMapFragment fragment) {
		if (sIsMeasuringLocation) {
			return;
		}
		if (sLocationManager == null) {
			sLocationManager = (LocationManager)fragment.getActivity().getSystemService(Context.LOCATION_SERVICE);
		}
		if (sLocationListener == null) {
			LocationFactory.sLocationListener = new LocationListener() {
				
				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {
				}
				
				@Override
				public void onProviderEnabled(String provider) {
				}
				
				@Override
				public void onProviderDisabled(String provider) {
				}
				
				@Override
				public void onLocationChanged(Location location) {
					if (isBetterLocation(location, sLocation)) {
						Log.d("LocationFactory.java", "Location Acquired: " + location.toString());
						sLocation = location;
						fragment.moveToLocation(sLocation);
					}
				}
			};
		}
		sLocation = null;
		
		sIsMeasuringLocation = true;

//		sLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, sLocationListener);
		sLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, sLocationListener);
	}
	public static Location getLocation(NearMapFragment fragment) {
		if (sIsMeasuringLocation == false) {
			startLocationMeasure(fragment);
		}
		return sLocation;	//however return location, check if null
	}
	public static void stopLocationMeasure() {
		if (sLocationManager != null && sLocationListener != null) {
			sLocationManager.removeUpdates(sLocationListener);
		}
	}
	
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	 
	private static boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        return true;
	    }
	 
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;
	 
	    if (isSignificantlyNewer) {
	        return true;
	    } else if (isSignificantlyOlder) {
	        return false;
	    }
	 
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;
	 
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());
	 
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}
	 
	private static boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}}
