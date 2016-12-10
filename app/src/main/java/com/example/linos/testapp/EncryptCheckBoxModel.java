package com.example.linos.testapp;

/**
 * Class for ecryption option selected by user
 * Created by linos on 12/9/2016.
 */

public class EncryptCheckBoxModel {
    String option;
   // int selected;
    boolean isChecked;

    EncryptCheckBoxModel(String option) {
        this.option = option;
       // this.selected = selected;
    }

    public String getOption(){
        return this.option;
    }

  /*  public int getSelected(){
        return this.selected;
    }*/

    public boolean getIsChecked(){
        return isChecked;
    }

    public void setIsChecked(boolean isChecked){
        this.isChecked = isChecked;
    }

}
