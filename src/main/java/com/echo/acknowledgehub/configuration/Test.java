package com.echo.acknowledgehub.configuration;

public class Test {
    public static void  main(String[]args){
       String str="NAME_kaung kaung_ID_2";
        String[]array=str.split("_");
        System.out.println("Name : "+array[1]);
        System.out.println("ID : "+array[3]);
    }
}
