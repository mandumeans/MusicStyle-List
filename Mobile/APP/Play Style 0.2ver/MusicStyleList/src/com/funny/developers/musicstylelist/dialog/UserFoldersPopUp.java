package com.funny.developers.musicstylelist.dialog;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.adapter.UserFolderPopUpAdapter;
import com.funny.developers.musicstylelist.dao.UserFoldersDao;
import com.funny.developers.musicstylelist.dao.UserPlayListDao;
import com.funny.developers.musicstylelist.definition.NotifyUrlDefine;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;
import com.funny.developers.musicstylelist.model.UserFoldersModel;

public class UserFoldersPopUp extends Dialog implements android.view.View.OnClickListener, OnItemClickListener {
	private ListView listFolder = null;
	private Button btnCancle = null;	
	private ArrayList<BaseModel> folderList = null;
	private UserFolderPopUpAdapter adapter = null;
	private Context context = null;
	private SearchTrackListModel model = null;
	private String folderName = null;

	public UserFoldersPopUp(Context context, int theme)
	{
		super(context, theme);
		this.context = context;
	}

	public UserFoldersPopUp(Context context)
	{
		super(context);
		this.context = context;
	}

	public void setModel(SearchTrackListModel model)
	{
		this.model = model;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		setContentView(R.layout.user_folder_popup);

		setDialogSize();

		View header = getLayoutInflater().inflate(R.layout.user_folder_list_header, null, false);
		listFolder = (ListView) findViewById(R.id.list_folder);
		listFolder.setOnItemClickListener(this);
		listFolder.addHeaderView(header);
		
		header.findViewById(R.id.layout_user_folder_header).setOnClickListener(this);

		btnCancle = (Button) findViewById(R.id.btn_cancel);
		btnCancle.setOnClickListener(this);

		setFolders();

	}

	private void setDialogSize()
	{
		LayoutParams lp = getWindow().getAttributes( ) ;
		WindowManager wm = ((WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)) ;
		lp.width =  (int)( wm.getDefaultDisplay().getWidth() * 0.90 );
		getWindow().setAttributes( lp ) ;
	}

	public void setFolders()
	{
		UserFoldersDao dao = new UserFoldersDao(context);
		folderList = dao.getData();
		dao.close();

		if(folderList != null) {
			if(listFolder.getAdapter() == null){
				adapter = new UserFolderPopUpAdapter(context); 
				adapter.setList(folderList);
				listFolder.setAdapter(adapter);
			}
			else
			{
				adapter.setList(folderList);
			}

			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		insertToFolderList(position);
	}

	public void insertToFolderList(int position) {
		model.folderNo = ((UserFoldersModel)folderList.get(position-1)).no;

		UserPlayListDao dao = new UserPlayListDao(context);
		dao.insert(model);
		dao.close();
		Toast.makeText(context, context.getString(R.string.insert_track) + model.title, Toast.LENGTH_SHORT).show();

		dismiss();
		context.getContentResolver().notifyChange(NotifyUrlDefine.URI_NOTIFY_DATASET_CHANGE, null);
	}
	
	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.btn_cancel)
		{
			dismiss();
		}
		else if(v.getId() == R.id.layout_user_folder_header)
		{
			AlertDialog.Builder alert = new AlertDialog.Builder(context);

			final EditText input = new EditText(context);
			alert.setTitle(getContext().getString(R.string.dialog_folder_title));
			alert.setView(input);

			alert.setPositiveButton(getContext().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					folderName = input.getText().toString().trim();
					if(folderName.length() > 0)
					{
						UserFoldersModel model = new UserFoldersModel();
						model.folderName = folderName;
						UserFoldersDao dao = new UserFoldersDao(context);
						dao.insert(model);
						dao.close();
	
						setFolders();
						insertToFolderList(listFolder.getCount()-1);
					}
					else
					{
						Toast.makeText(context, getContext().getString(R.string.dialog_folder_title), Toast.LENGTH_SHORT).show();
					}
				} 
			}); 
			alert.setNegativeButton(getContext().getString(R.string.dialog_cancle), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			}); 
			AlertDialog alertDialog = alert.create();
			alertDialog.show();
			
		}
	}

}
