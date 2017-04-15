/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.test.topterms;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TopTerm {
    
    String field;
    String term;
    int count;

    public TopTerm(String field, String term, int count) {
        this.field = field;
        this.term = term;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public String getTerm() {
        return term;
    }

    public String getField() {
        return field;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TopTerm other = (TopTerm) obj;
        if ((this.field == null) ? (other.field != null) : !this.field.equals(other.field)) {
            return false;
        }
        if ((this.term == null) ? (other.term != null) : !this.term.equals(other.term)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.field != null ? this.field.hashCode() : 0);
        hash = 47 * hash + (this.term != null ? this.term.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s", count, term);
    }
}