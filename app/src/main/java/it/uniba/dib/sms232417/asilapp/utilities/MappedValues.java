package it.uniba.dib.sms232417.asilapp.utilities;

import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import it.uniba.dib.sms232417.asilapp.R;

public class MappedValues {
    private Map<Integer, String> mappedHowToTake;
    private Map<Integer, String> mappedHowRegularly;
    private Map<Integer, String> mappedInterval;

    public MappedValues(Context context) {
        // Default constructor
        mappedHowToTake = new HashMap<>();
        mappedHowRegularly = new HashMap<>();
        mappedInterval = new HashMap<>();

        int i;

        String[] howToTakeArray = context.getResources().getStringArray(R.array.how_to_take_medicine_list);
        for (i = 0; i < howToTakeArray.length; i++) {
            mappedHowToTake.put(i, howToTakeArray[i]);
        }

        String[] howRegularlyArray = context.getResources().getStringArray(R.array.how_regularly_list);
        for (i = 0; i < howRegularlyArray.length; i++) {
            mappedHowRegularly.put(i, howRegularlyArray[i]);
        }

        mappedInterval.put(0, context.getResources().getString(R.string.day));
        mappedInterval.put(1, context.getResources().getString(R.string.week));
        mappedInterval.put(2, context.getResources().getString(R.string.month));
    }

    public String getHowToTake(int key) {
        return mappedHowToTake.get(key);
    }

    public String getHowRegularly(int key) {
        return mappedHowRegularly.get(key);
    }

    public int getHowToTakeKey(String value) {
        int i;
        for (i = 0; i < mappedHowToTake.size(); i++) {
            if (mappedHowToTake.get(i).equals(value)) {
                return i;
            }
        }
        return -1;
    }

    public int getHowRegularlyKey(String value) {
        int i;
        for (i = 0; i < mappedHowRegularly.size(); i++) {
            if (mappedHowRegularly.get(i).equals(value)) {
                return i;
            }
        }
        return -1;
    }

    public String getInterval(int key) {
        return mappedInterval.get(key);
    }

    public int getIntervalKey(String value) {
        for (int i = 0; i < mappedInterval.size(); i++) {
            if (mappedInterval.get(i).equals(value)) {
                return i;
            }
        }
        return -1;
    }
}
