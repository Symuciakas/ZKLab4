package com.example.projectchibi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.jar.Attributes;

public class Chibi implements Parcelable {
    private int id;
    private String name;
    private String nickname;
    private Bitmap image;
    private int maxHp;
    private int currentHp;
    private int defense;
    private int affection;
    private int energy;
    private int hygiene;
    private int hunger;

    public Chibi()
    {
        id = 0;
        name = "Jeanne D'arc";
        nickname = "Jalter";
        maxHp = 1;
        currentHp = maxHp + 0;

        defense = 0;
        affection = 0;
        energy = 1;
        hygiene = 0;
        hunger = 0;
    }

    protected Chibi(Parcel source)
    {
        id = source.readInt();
        name = source.readString();
        nickname = source.readString();
        maxHp = source.readInt();
        currentHp = source.readInt();
        defense = source.readInt();
        affection = source.readInt();
        energy = source.readInt();
        hygiene = source.readInt();
        hunger = source.readInt();
    }

    public static final Creator<Chibi> CREATOR = new Creator<Chibi>() {
        @Override
        public Chibi createFromParcel(Parcel source) {
            return new Chibi(source);
        }

        @Override
        public Chibi[] newArray(int size) {
            return new Chibi[size];
        }
    };

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getNickname()
    {
        return nickname;
    }
    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public int getMaxHp()
    {
        return maxHp;
    }
    public void setMaxHp(int maxHp)
    {
        this.maxHp = maxHp;
    }

    public Bitmap getImage()
    {
        return image;
    }
    public void setImage(Bitmap image)
    {
        this.image = image;
    }

    public int getCurrentHp()
    {
        return currentHp;
    }
    public void setCurrentHp(int currentHp)
    {
        this.currentHp = currentHp;
    }

    public int getDefense()
    {
        return defense;
    }

    public int getAffection()
    {
        return affection;
    }
    public void setAffection(int affection)
    {
        this.affection = affection;
    }

    public int getEnergy()
    {
        return energy;
    }
    public void setEnergy(int energy)
    {
        this.energy = energy;
    }

    public int getHygiene()
    {
        return hygiene;
    }
    public void setHygiene(int hygiene)
    {
        this.hygiene = hygiene;
    }

    public int getHunger()
    {
        return hunger;
    }
    public void setHunger(int hunger)
    {
        this.hunger = hunger;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(nickname);
        dest.writeInt(maxHp);
        dest.writeInt(currentHp);
        dest.writeInt(defense);
        dest.writeInt(affection);
        dest.writeInt(energy);
        dest.writeInt(hygiene);
        dest.writeInt(hunger);
    }
}
