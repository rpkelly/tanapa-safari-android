package edu.clemson.tanapasafari;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;
import edu.clemson.tanapasafari.constants.Constants;
import edu.clemson.tanapasafari.db.TanapaDbHelper;
import edu.clemson.tanapasafari.model.Media;
import edu.clemson.tanapasafari.model.Report;
import edu.clemson.tanapasafari.model.ReportType;
import edu.clemson.tanapasafari.model.User;
import edu.clemson.tanapasafari.model.UserIdListener;
import edu.clemson.tanapasafari.service.GPSTracker;
import edu.clemson.tanapasafari.service.GPSTrackerSingleton;


public class ReportActivity extends Activity {

	// Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
 
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "TANAPA_SAFARI_IMAGES";
 
    private Uri fileUri; // file url to store image/video
    private String fileType; // mime type of the file represented by the file URI.
    
    private ImageView imgPreview;
    private VideoView videoPreview;
    private Button btnCapturePicture; 
    private Button btnRecordVideo;
    
 
	private final OnClickListener saveButtonOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			saveReport();
		}
		
	};
	
	
	
	
	private final OnClickListener btnCapturePictureOnClickListener = new OnClickListener() {
		 
        @Override
        public void onClick(View v) {
            // capture picture
            captureImage();
        }
        
    };
    
    private final OnClickListener btnRecordVideoOnClickListener = new View.OnClickListener() {
    	 
        @Override
        public void onClick(View v) {
            // record video
            recordVideo();
        }
    };
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		
		Button saveButton = (Button) findViewById(R.id.report_saveButton);
		saveButton.setOnClickListener(saveButtonOnClickListener);
		
		imgPreview = (ImageView) findViewById(R.id.report_imgPreview);
        videoPreview = (VideoView) findViewById(R.id.report_videoPreview);
        btnCapturePicture = (Button) findViewById(R.id.report_btnCapturePicture);
        btnRecordVideo = (Button) findViewById(R.id.report_btnRecordVideo);
 
        btnCapturePicture.setOnClickListener(btnCapturePictureOnClickListener);
        btnRecordVideo.setOnClickListener(btnRecordVideoOnClickListener);
        
        List<ReportType> reportTypes = TanapaDbHelper.getInstance(this).getReportTypes();
        setReportTypeSpinnerValues(reportTypes);
        
	}
	
	
	private void setReportTypeSpinnerValues(List<ReportType> values) {
		Spinner reportTypeSpinner = (Spinner) this.findViewById(R.id.report_reportTypeSpinner);
		ArrayAdapter<ReportType> aa = new ArrayAdapter<ReportType>(this, android.R.layout.simple_list_item_single_choice, values);
		reportTypeSpinner.setAdapter(aa);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.report, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void saveReportForUserId(int userId) {
		// Save report data to local database
		Report report = new Report();
		Media media = null;
		Spinner reportTypeSpinner = (Spinner) findViewById(R.id.report_reportTypeSpinner);
		ReportType reportType = (ReportType) reportTypeSpinner.getSelectedItem();
		EditText contentEditText = (EditText) findViewById(R.id.report_content);
		report.setReportTypeId(reportType.getId());
		report.setContent(contentEditText.getText().toString());
		report.setTime(new Date());
		report.setUserId(userId);
		GPSTracker gps = GPSTrackerSingleton.getInstance(this);
		if (gps.canGetLocation()) {
			Location location = gps.getLocation();
			Log.d(Constants.LOGGING_TAG, "Report Location: " + location.getLatitude() + ", " + location.getLongitude());
			report.setLatitude(location.getLatitude());
			report.setLongitude(location.getLongitude());
		}
		if (fileUri != null) {
			media = new Media();
			media.setUrl(fileUri.getPath());
			media.setType(fileType);
			report.setMedia(media);
		}
		long reportId = TanapaDbHelper.getInstance(this).saveReport(report);
		Log.d(Constants.LOGGING_TAG, "Saved report locally with ID of: " + reportId);
	}
	
	private void saveReport() {
		
		User.getId(this, new UserIdListener() {

			@Override
			public void onUserId(Integer id) {
				saveReportForUserId(id);
			}
			
		});
		
	}
	
	
	/**
     * Capturing Camera Image will lauch camera app requrest image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
 
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        fileType = "image/jpeg";
 
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
 
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }
    
    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
 
        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }
 
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
 
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }
 
    /**
     * Recording video
     */
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
 
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        fileType = "video/mp4";
 
        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
 
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
                                                            // name
 
        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }
 
    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // video successfully recorded
                // preview the recorded video
                previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
 
    /**
     * Display image from a path to ImageView
     */
    private void previewCapturedImage() {
        try {
            
        	btnCapturePicture.setVisibility(View.GONE);
        	btnRecordVideo.setVisibility(View.GONE);
        	videoPreview.setVisibility(View.GONE);
            imgPreview.setVisibility(View.VISIBLE);
            
 
            
 
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();
 
            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 2;
 
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);
 
            imgPreview.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * Previewing recorded video
     */
    private void previewVideo() {
        try {
            
        	btnCapturePicture.setVisibility(View.GONE);
        	btnRecordVideo.setVisibility(View.GONE);
        	imgPreview.setVisibility(View.GONE);
            videoPreview.setVisibility(View.VISIBLE);
 
            videoPreview.setVideoPath(fileUri.getPath());
            // start playing
            videoPreview.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     
    /**
     * ------------ Helper Methods ---------------------- 
     * */
 
    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
 
    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {
 
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
 
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
 
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
 
        return mediaFile;
    }

}
