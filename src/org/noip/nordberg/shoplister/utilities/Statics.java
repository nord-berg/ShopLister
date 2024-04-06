package org.noip.nordberg.shoplister.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.util.SparseIntArray;

import com.mobeta.android.dslv.DragSortListView;

public class Statics {
	public static String currentListName;
	public static Typeface robotoSerifTypeFace;
	public static Typeface robotoSerifBoldTypeFace;
	public static Typeface robotoSerifLightTypeFace;
	public static Typeface robotoSerifThinTypeFace;
	public static Boolean prefShowQuantity;
	public static String prefDragHandleSide;
	public static Editor prefEditor;
	public static Boolean sortByTotal;
	public static ArrayList<String> dropdownValues;
	public static boolean extrasExposed;
	public static int extrasExposedRow;
	public static DragSortListView currentListView;
	public static boolean cleaningRequested;
	public static List<Integer> rowsToHideAfterAnim;
	public static List<Integer> preDropSortRanksFull;
    public static SparseIntArray dragNDropMap;
    public static Set<Integer> checkedRows;
	
}
