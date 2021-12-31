package com.ghgande.j2mod.modbus.procimg;

public class ObservableDataBean {

    private int address;
    private int function;
    private int dataType;
    private String value;
    private int shortPosition;
    private int numberOfShorts;

    public ObservableDataBean() {
    }


    public ObservableDataBean(int address, int function, int dataType){
        this.address=address;
        this.function=function;
        this.dataType=dataType;
        this.shortPosition=0;
        this.numberOfShorts=1;

    }

    public ObservableDataBean(int address, int function, int dataType,int shortPosition, int numberOfShorts){
        this.address=address;
        this.function=function;
        this.dataType=dataType;
        this.shortPosition=shortPosition;
        this.numberOfShorts=numberOfShorts;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getFunction() {
        return function;
    }

    public void setFunction(int function) {
        this.function = function;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getShortPosition() {
        return shortPosition;
    }

    public void setShortPosition(int shortPosition) {
        this.shortPosition = shortPosition;
    }

    public int getNumberOfShorts() {
        return numberOfShorts;
    }

    public void setNumberOfShorts(int numberOfShorts) {
        this.numberOfShorts = numberOfShorts;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ObservableDataBean{" +
                "address=" + address +
                ", function=" + function +
                ", dataType=" + dataType +
                ", value='" + value + '\'' +
                ", shortPosition=" + shortPosition +
                ", numberOfShorts=" + numberOfShorts +
                '}';
    }
}
