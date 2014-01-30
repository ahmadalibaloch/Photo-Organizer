package fragment;

import com.baloch.photoorganizer.R;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Fragment_GalleryList extends ListFragment {

	/**
	 * Array with strings to show in list
	 */
	private String dataArray[];

	public Fragment_GalleryList() {
		dataArray = new String[] { "One", "Two", "Three", };
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListAdapter listAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, dataArray);
		setListAdapter(listAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_gallery_list, container,
				false);
	}

	@Override
	public void onListItemClick(ListView list, View v, int position, long id) {
		Toast.makeText(getActivity(),
				getListView().getItemAtPosition(position).toString(),
				Toast.LENGTH_LONG).show();
	}
}
