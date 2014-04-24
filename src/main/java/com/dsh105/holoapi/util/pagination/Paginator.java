/*
 * This file is part of HoloAPI.
 *
 * HoloAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HoloAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HoloAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dsh105.holoapi.util.pagination;

import java.util.ArrayList;


public class Paginator {

    private ArrayList<String> raw = new ArrayList<String>();
    private int perPage;

    public Paginator(String[] raw, int perPage) {
        this.perPage = perPage;
        this.setRaw(raw);
    }

    public Paginator(ArrayList<String> raw, int perPage) {
        this.perPage = perPage;
        this.setRaw(raw);
    }

    public void setRaw(String[] newRaw) {
        for (String s : newRaw) {
            this.raw.add(s);
        }
    }

    public ArrayList<String> getRaw() {
        return this.raw;
    }

    public void setRaw(ArrayList<String> newRaw) {
        this.raw = newRaw;
    }

    public int getIndex() {
        return (int) (Math.ceil(this.raw.size() / ((double) this.perPage)));
    }

    public double getDoubleIndex() {
        return (Math.ceil(this.raw.size() / ((double) this.perPage)));
    }

    public String[] getPage(int pageNumber) {
        int index = this.perPage * (Math.abs(pageNumber) - 1);
        ArrayList<String> list = new ArrayList<String>();
        if (pageNumber <= getDoubleIndex()) {
            for (int i = index; i < (index + this.perPage); i++) {
                if (raw.size() > i) {
                    list.add(raw.get(i));
                }
            }
        } else {
            return null;
        }
        return list.toArray(new String[list.size()]);
    }
}