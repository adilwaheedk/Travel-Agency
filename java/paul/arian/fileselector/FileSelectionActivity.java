package paul.arian.fileselector;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;
import com.visionxoft.abacus.rehmantravel.R;

public class FileSelectionActivity extends AppCompatActivity {

    public static final String FILES_TO_UPLOAD = "upload";
    private static final String TAG = "FileSelection";
    private File mainPath = new File(Environment.getExternalStorageDirectory() + "");
    private ArrayList<File> resultFileList;

    private ListView directoryView;
    private ArrayList<File> directoryList = new ArrayList<>();
    private ArrayList<String> directoryNames = new ArrayList<>();
    //private ListView fileView;
    private ArrayList<File> fileList = new ArrayList<>();
    private ArrayList<String> fileNames = new ArrayList<>();
    private Button fs_ok, fs_all, fs_cancel, fs_storage, fs_new_folder;
    private TextView fs_folderpath;
    private Boolean Switch = false, switcher = false;
    private String primary_sd, secondary_sd;
    private int index = 0, top = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selection);

        directoryView = (ListView) findViewById(R.id.directorySelectionList);
        fs_ok = (Button) findViewById(R.id.fs_ok);
        fs_all = (Button) findViewById(R.id.fs_all);
        fs_cancel = (Button) findViewById(R.id.fs_cancel);
        fs_storage = (Button) findViewById(R.id.fs_storage);
        fs_new_folder = (Button) findViewById(R.id.fs_new_folder);
        fs_folderpath = (TextView) findViewById(R.id.fs_folderpath);

        fs_new_folder.setText("Back");
        fs_new_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        loadLists();

        ExtStorageSearch();
        if (secondary_sd == null) {
            fs_storage.setEnabled(false);
        }

        directoryView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                index = directoryView.getFirstVisiblePosition();
                View v = directoryView.getChildAt(0);
                top = (v == null) ? 0 : v.getTop();

                File lastPath = mainPath;
                try {
                    if (position < directoryList.size()) {
                        mainPath = directoryList.get(position);
                        loadLists();
                    }
                } catch (Throwable e) {
                    mainPath = lastPath;
                    loadLists();
                }
            }
        });

        fs_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ok_button();
            }
        });

        fs_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        fs_storage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (!switcher) {
                        mainPath = new File(secondary_sd);
                        loadLists();
                        switcher = true;
                        fs_storage.setText(getString(R.string.Int));
                    } else {
                        mainPath = new File(primary_sd);
                        loadLists();
                        switcher = false;
                        fs_storage.setText(getString(R.string.ext));
                    }
                } catch (Throwable e) {

                }
            }
        });

        fs_all.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!Switch) {
                    for (int i = directoryList.size(); i < directoryView.getCount(); i++) {
                        directoryView.setItemChecked(i, true);
                    }
                    fs_all.setText(getString(R.string.none));
                    Switch = true;
                } else {
                    for (int i = directoryList.size(); i < directoryView.getCount(); i++) {
                        directoryView.setItemChecked(i, false);
                    }
                    fs_all.setText(getString(R.string.all));
                    Switch = false;
                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        try {
            if (mainPath.equals(Environment.getExternalStorageDirectory().getParentFile().getParentFile())) {
                finish();
            } else {
                mainPath = mainPath.getParentFile();
                loadLists();
                directoryView.setSelectionFromTop(index, top);
            }

        } catch (Throwable e) {

        }
    }

    public void ok_button() {
        resultFileList = new ArrayList<>();

        for (int i = 0; i < directoryView.getCount(); i++) {
            if (directoryView.isItemChecked(i)) {
                resultFileList.add(fileList.get(i - directoryList.size()));
            }
        }
        if (resultFileList.isEmpty()) {
            Log.d(TAG, "Nothing selected");
            finish();
        }
        Log.d(TAG, "Files: " + resultFileList.toString());
        Intent result = this.getIntent();
        result.putExtra(FILES_TO_UPLOAD, resultFileList);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    private void loadLists() {
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        };
        FileFilter directoryFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };

        //if(mainPath.exists() && mainPath.length()>0){
        //Lista de directorios
        File[] tempDirectoryList = mainPath.listFiles(directoryFilter);

        if (tempDirectoryList != null && tempDirectoryList.length > 1) {
            Arrays.sort(tempDirectoryList, new Comparator<File>() {
                @Override
                public int compare(File object1, File object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
        }

        directoryList = new ArrayList<>();
        directoryNames = new ArrayList<>();
        for (File file : tempDirectoryList) {
            directoryList.add(file);
            directoryNames.add(file.getName());
        }
        ArrayAdapter<String> directoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, directoryNames);

        //Lista de ficheros
        File[] tempFileList = mainPath.listFiles(fileFilter);

        if (tempFileList != null && tempFileList.length > 1) {
            Arrays.sort(tempFileList, new Comparator<File>() {
                @Override
                public int compare(File object1, File object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
        }

        fileList = new ArrayList<>();
        fileNames = new ArrayList<>();
        for (File file : tempFileList) {
            fileList.add(file);
            fileNames.add(file.getName());
        }


        fs_folderpath.setText(mainPath.toString());
        loadIcons();
        setTitle(mainPath.getName());
        //}
    }

    /**
     * @Override public boolean onCreateOptionsMenu(Menu menu) {
     * getMenuInflater().inflate(R.menu.activity_file_selection, menu);
     * return true;
     * }
     * @Override public boolean onOptionsItemSelected(MenuItem item) {
     * switch (item.getItemId()) {
     * case android.R.id.home:
     * NavUtils.navigateUpFromSameTask(this);
     * return true;
     * }
     * return super.onOptionsItemSelected(item);
     * }
     **/

    public void loadIcons() {
        String[] foldernames = new String[directoryNames.size()];
        foldernames = directoryNames.toArray(foldernames);

        String[] filenames = new String[fileNames.size()];
        filenames = fileNames.toArray(filenames);

        CustomListSingleOnly adapter1 = new CustomListSingleOnly(FileSelectionActivity.this, directoryNames.toArray(foldernames), mainPath.getPath());
        CustomList adapter2 = new CustomList(FileSelectionActivity.this, fileNames.toArray(filenames), mainPath.getPath());

        MergeAdapter adap = new MergeAdapter();

        adap.addAdapter(adapter1);
        adap.addAdapter(adapter2);


        directoryView.setAdapter(adap);
    }

    public void ExtStorageSearch() {
        String[] extStorlocs = {"/storage/sdcard1", "/storage/extsdcard", "/storage/sdcard0/external_sdcard", "/mnt/extsdcard",
                "/mnt/sdcard/external_sd", "/mnt/external_sd", "/mnt/media_rw/sdcard1", "/removable/microsd", "/mnt/emmc",
                "/storage/external_SD", "/storage/ext_sd", "/storage/removable/sdcard1", "/data/sdext", "/data/sdext2",
                "/data/sdext3", "/data/sdext4", "/storage/sdcard0"};

        //First Attempt
        primary_sd = System.getenv("EXTERNAL_STORAGE");
        secondary_sd = System.getenv("SECONDARY_STORAGE");

        if (primary_sd == null) {
            primary_sd = Environment.getExternalStorageDirectory() + "";
        }
        if (secondary_sd == null) {
            //if fail, search among known list of extStorage Locations
            for (String string : extStorlocs) {
                if ((new File(string)).exists() && (new File(string)).isDirectory()) {
                    secondary_sd = string;
                    break;
                }
            }
        }

    }


}
