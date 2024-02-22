package it.uniba.dib.sms232417.asilapp.utilities;

import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.util.Log;

import it.uniba.dib.sms232417.asilapp.R;

public class MappedValues {
    private Map<Integer, String> mappedHowToTake;
    private Map<Integer, String> mappedHowRegularly;
    private Map<Integer, String> mappedInterval;
    private Context context;

    public MappedValues(Context context) {
        this.context = context;
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

    public String getFormattedHowToTake(int howToTake, int quantity) {
        String formattedHowToTake;

        Log.d("Quantity_getFormattedHowToTake", "HowToTake: " + howToTake + " Quantity: " + quantity);

        switch (howToTake) {
            case 0:
                // Tablet/s
                formattedHowToTake = context.getResources().getQuantityString(R.plurals.tablet, quantity, quantity);
                break;
            case 1:
                // Drop/s
                formattedHowToTake = context.getResources().getQuantityString(R.plurals.drop, quantity, quantity);
                break;
            case 2:
                // Sachet/s
                formattedHowToTake = context.getResources().getQuantityString(R.plurals.sachet, quantity, quantity);
                break;
            case 3:
                // Suppository/ies
                formattedHowToTake = context.getResources().getQuantityString(R.plurals.suppository, quantity, quantity);
                break;
            case 4:
                // Milliliters
                formattedHowToTake = context.getResources().getQuantityString(R.plurals.milliliter, quantity, quantity);
                break;
            case 5:
                // Syringe/s
                formattedHowToTake = context.getResources().getQuantityString(R.plurals.syringe, quantity, quantity);
                break;
            case 6:
                // Inhalation
                formattedHowToTake = context.getResources().getQuantityString(R.plurals.inhalation, quantity, quantity);
                break;
            default:
                formattedHowToTake = "";
                break;

        }

        return formattedHowToTake;
    }

    public String getFormattedInterval(int intervalSelectedType, int intervalSelectedNumber) {
        String formattedInterval;

        formattedInterval = context.getResources().getString(R.string.every) + " " + intervalSelectedNumber + " ";
        switch (intervalSelectedType) {
            case 0:
                // Days
                formattedInterval = formattedInterval + context.getResources().getQuantityString(R.plurals.days, intervalSelectedNumber, intervalSelectedNumber);
                break;
            case 1:
                // Weeks
                formattedInterval = formattedInterval + context.getResources().getQuantityString(R.plurals.weeks, intervalSelectedNumber, intervalSelectedNumber);
                break;
            case 2:
                // Months
                formattedInterval = formattedInterval + context.getResources().getQuantityString(R.plurals.months, intervalSelectedNumber, intervalSelectedNumber);
                break;
            default:
                formattedInterval = "";
                break;
        }

        return formattedInterval;

    }
}
