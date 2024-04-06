package org.noip.nordberg.shoplister.dragndrop;

import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.CHECKED;
import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.ROW_ID;
import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.SORT_RANK;

import java.util.ArrayList;
import java.util.List;

import org.noip.nordberg.shoplister.asynctasks.queries.SetSortRankAsyncTask;
import org.noip.nordberg.shoplister.database.CursorProvider;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.database.Cursor;

public class DragNDropUtils {
	
	/**
	 * @param sPos - Source position in list
	 * @param dPos - Destination position in list
	 * @param cursorProvider
	 */
	public static void actionDrop(int sPos, int dPos, CursorProvider cursorProvider){
		// Don't allow dragging while cleaning
		if(Statics.cleaningRequested) return;
		
		int firstCheckedRow = -1;
		int rowIdSource;
		List<Integer> postDropSortRanks;
		Integer sortRankSource;
		Integer sortRankDestination;
		Cursor cursor = cursorProvider.getCursor();

		// Find the sortRank, rowId for source and destination rows
		cursor.moveToPosition(dPos);
		sortRankDestination = cursor.getInt(SORT_RANK);
		
		cursor.moveToPosition(sPos);
		sortRankSource = cursor.getInt(SORT_RANK);
		rowIdSource = cursor.getInt(ROW_ID);
		if (cursor.getInt(CHECKED) == 1) return;  // quit if source is checked *try without this code too*
		
		// Find first checked item in list
		cursor.moveToFirst();
		do {
			if(cursor.getInt(CHECKED) == 1){
				firstCheckedRow = cursor.getPosition();
				break;
			}
		} while (cursor.moveToNext());
		
		// Don't allow dropping onto checked items
		if(firstCheckedRow != -1 && dPos >= firstCheckedRow){
			dPos = firstCheckedRow - 1; 
		}
		
		// Let's get this party started.  Swap the rows.
		// This might seem a bit convoluted but it's just chopping up a bunch of lists
		// and stitching them back together.

		// 1. Find important locations in preDropSortRanksFull array
		int sourceIndex = Statics.preDropSortRanksFull.indexOf(sortRankSource);
		int destinationIndex = Statics.preDropSortRanksFull.indexOf(sortRankDestination);
		if (sourceIndex == destinationIndex) return; // save some battrezies

		// 2. Chuck them together into an ArrayList 
		postDropSortRanks = new ArrayList<Integer>();
		List<Integer> preDropSortRanks = new ArrayList<Integer>();
		List<Integer> postDropSortRanksWithoutDestination = new ArrayList<Integer>();

		// List build technique depends on drag direction:
		if (sourceIndex > destinationIndex){  // upward drag
			// Crop the preDropRows list to affected rows only
			preDropSortRanks = Statics.preDropSortRanksFull.subList(destinationIndex, sourceIndex + 1);
			// Make the postDropRows list
			postDropSortRanksWithoutDestination = Statics.preDropSortRanksFull.subList(destinationIndex + 1, sourceIndex + 1); 
			// Build the postDropRows list
			postDropSortRanks.addAll(postDropSortRanksWithoutDestination);
			postDropSortRanks.add(sortRankDestination);
		}
		else { // downward drag
			// Crop the preDropRows list to affected rows only
			preDropSortRanks = Statics.preDropSortRanksFull.subList(sourceIndex, destinationIndex + 1);
			// Make the sublists
			postDropSortRanksWithoutDestination = Statics.preDropSortRanksFull.subList(sourceIndex, destinationIndex); // not really "post drop" yet.. keep reading
			// Build the postDropRows list
			postDropSortRanks.add(sortRankDestination);
			postDropSortRanks.addAll(postDropSortRanksWithoutDestination);
		}

		// 3. Write new rows to DB
		int postDropSortRanksSize = postDropSortRanks.size();
		if (sourceIndex > destinationIndex){ // upward drag
			// Iterate through list, modifying sortRank of each item (except the sourceSortRank because we'll add that after)
			for(int i = 0; i < postDropSortRanksSize -1 ; i++){
				new SetSortRankAsyncTask(getRowIdFromSortRank(preDropSortRanks.get(i), cursor), postDropSortRanks.get(i)).execute();
			}
			// Modify source SortRank
			new SetSortRankAsyncTask(rowIdSource,sortRankDestination).execute();
		}
		else { 
			if (sourceIndex < destinationIndex){ // downward drag
				// Iterate through list, modifying sortRank of each item (except the source SortRank because we'll add that after)
				for(int i = 1; i < postDropSortRanksSize; i++){
					new SetSortRankAsyncTask(getRowIdFromSortRank(preDropSortRanks.get(i), cursor), postDropSortRanks.get(i)).execute();
				}
				// Modify source SortRank
				new SetSortRankAsyncTask(rowIdSource,postDropSortRanks.get(0)).execute();
			}
		}
		return;
	}
	
	public static int getRowIdFromSortRank(int sortRank, Cursor cursor){
		int count = cursor.getCount();
		for(int i = 0; i < count; i++){
			cursor.moveToPosition(i);
			int cursorOutput = cursor.getInt(SORT_RANK);
			if (cursorOutput == sortRank) return cursor.getInt(ROW_ID);
		}
		return -1; // Shouldn't ever get here
	}
	
	/** Resets dragNDropMap to 1:1 map.  eg: (1,1)(2,2),etc
	 * 
	 */
	public static void cleanListMapping(){
		int dragNDropMapSize = Statics.currentListView.getCount() - 1;  // -1 to exclude footer
		for (int i = 0; i < dragNDropMapSize; i++) {
			Statics.dragNDropMap.put(i, i);
		}
		
	}
}
