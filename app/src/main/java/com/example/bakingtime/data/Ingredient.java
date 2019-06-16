package com.example.bakingtime.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingredient implements Parcelable {

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
    // Quantity for each single ingredient
    private int quantity;
    // Measure for each single ingredient
    private String measure;
    // The single ingredient
    private String singleIngredient;

    public Ingredient(int quantity, String measure, String singleIngredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.singleIngredient = singleIngredient;
    }

    protected Ingredient(Parcel in) {
        quantity = in.readInt();
        measure = in.readString();
        singleIngredient = in.readString();
    }

    public String getIngredient() {
        return quantity + " " + measure + " " + singleIngredient;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(quantity);
        dest.writeString(measure);
        dest.writeString(singleIngredient);
    }
}
