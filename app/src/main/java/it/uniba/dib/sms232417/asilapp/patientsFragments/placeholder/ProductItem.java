package it.uniba.dib.sms232417.asilapp.patientsFragments.placeholder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProductItem {
    private String nameProduct;
    private String quantityProduct;

    // Constructor, getters and setters...


    public ProductItem(String nameProduct, String quantityProduct) {
        this.nameProduct = nameProduct;
        this.quantityProduct = quantityProduct;

    }

    public String getQuantityProduct() {
        return quantityProduct;
    }

    public void setQuantityProduct(String quantityProduct) {
        this.quantityProduct = quantityProduct;
    }
    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }


}

