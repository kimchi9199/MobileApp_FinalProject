package com.example.mobileapp_final.Model;

import java.util.ArrayList;

public class FaceVector {
    public String Name;
    public ArrayList<float[][]> VectorsOfFace = new ArrayList<>();

    public FaceVector(String Name) {
        this.Name = Name;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<float[][]> getVectorsOfFace() {
        return VectorsOfFace;
    }

    public void setVectorsOfFace(ArrayList<float[][]> vectorsOfFace) {
        VectorsOfFace = vectorsOfFace;
    }
}
