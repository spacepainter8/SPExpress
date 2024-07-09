/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import rs.etf.sab.operations.PackageOperations;
import rs.etf.sab.operations.PackageOperations.Pair;

/**
 *
 * @author sofija
 */
public class bs200051_PackageOperationsPair<A,B> implements PackageOperations.Pair<A,B> {
    private A firstParam;
    private B secondParam;
    
    public bs200051_PackageOperationsPair(A firstParam, B secondParam){
        this.firstParam = firstParam;
        this.secondParam = secondParam;
    }
    
    @Override
    public A getFirstParam() {
        return firstParam;
    }

    @Override
    public B getSecondParam() {
        return secondParam;
    }

    @Override
    public String toString() {
        return "{" + firstParam + ", " + secondParam + '}';
    }
    
    static boolean equals(PackageOperations.Pair a, PackageOperations.Pair b){
        return a.getFirstParam().equals(b.getFirstParam()) && a.getSecondParam().equals(b.getSecondParam());
    }
    
    
    
}
