package com.pdachoice.commonwidgets;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.VideoView;

public class ScreenOneFragment extends Fragment implements OnClickListener,
    OnEditorActionListener, OnSeekBarChangeListener,
    CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener,
    OnMenuItemClickListener, OnItemSelectedListener, OnPreparedListener { // confront
                                                                          // to
                                                                          // interface
  private View contentView;
  private TextView mTextView;
  private EditText mEditTextOneLine;
  private EditText mEditTextFourLine;
  private Button mButton;
  private RadioGroup mRadioGroup;
  // private RadioButton mRadio0;
  // private RadioButton mRadio1;
  // private RadioButton mRadio2;
  private SeekBar mSeekBar;
  private TextView mTextViewSlider;
  private ProgressBar mProgressBar1;
  private ProgressBar mProgressBar2;
  private CompoundButton mSwitchButton;
  private ImageView mImageView;
  private Spinner mSpinner;
  private VideoView mVideoView;

  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  @SuppressWarnings("deprecation")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    contentView = inflater.inflate(R.layout.screenone_fragment, container,
        false);

    // TextView
    mTextView = (TextView) contentView.findViewById(R.id.textViewLabel);
    mTextView.setText("My " + mTextView.getText());

    // EditText
    mEditTextOneLine = (EditText) contentView
        .findViewById(R.id.editTextOneLine);
    mEditTextFourLine = (EditText) contentView
        .findViewById(R.id.editTextFourLine);

    String tmp = mEditTextOneLine.getText().toString();
    mEditTextOneLine.setText(tmp);

    mEditTextOneLine.setOnEditorActionListener(this);

    // Button
    mButton = (Button) contentView.findViewById(R.id.buttonAction);
    mButton.setOnClickListener(this);
    
    // RadioGroup
    mRadioGroup = (RadioGroup) contentView.findViewById(R.id.radioGroup);
    mRadioGroup.setOnCheckedChangeListener(this);

    // mRadio0 = (RadioButton) contentView.findViewById(R.id.radio0);
    // mRadio1 = (RadioButton) contentView.findViewById(R.id.radio1);
    // mRadio2 = (RadioButton) contentView.findViewById(R.id.radio2);

    // SeekBar
    mSeekBar = (SeekBar) contentView.findViewById(R.id.seekBar);
    mSeekBar.setOnSeekBarChangeListener(this);

    mTextViewSlider = (TextView) contentView.findViewById(R.id.textViewSlider);
    mTextViewSlider.setText(String.valueOf(mSeekBar.getProgress()));

    // ProgressBar
    mProgressBar1 = (ProgressBar) contentView.findViewById(R.id.progressBar1);
    mProgressBar2 = (ProgressBar) contentView.findViewById(R.id.progressBar2);

    // CompoundButton
    mSwitchButton = (CompoundButton) contentView
        .findViewById(R.id.switchButton);
    mSwitchButton.setOnCheckedChangeListener(this);

    // ImageView
    mImageView = (ImageView) contentView.findViewById(R.id.imageView);
    Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(
        R.drawable.iosadt)).getBitmap();

    Display display = getActivity().getWindowManager().getDefaultDisplay();

    int desireWidth;
    if (android.os.Build.VERSION.SDK_INT >= 13) {
      Point out = new Point();
      display.getSize(out);
      desireWidth = out.x;
    } else {
      desireWidth = display.getWidth();
    }

    scaleToFitWidth(bitmap, mImageView, desireWidth);

    // Options Menu
    setHasOptionsMenu(true); // need to enable it explicitly

    // Context Menu
    registerForContextMenu(mTextView);
    registerForContextMenu(mButton);

    // Spinner and its Adapter
    mSpinner = (Spinner) contentView.findViewById(R.id.spinner);
    // datasource Adapter
    String[] datasource = getResources().getStringArray(R.array.spinnerItems);

    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
        android.R.layout.simple_list_item_1, datasource);

    // The layout of the drop-down list items
    adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);

    // Set the adapter and initial selection.
    mSpinner.setAdapter(adapter);
    mSpinner.setSelection(1);

    // Respond to user selections
    mSpinner.setOnItemSelectedListener(this);

    // VideoView
    mVideoView = (VideoView) contentView.findViewById(R.id.videoView);

    // a. set video source, and register for video loaded/prepared
    Uri demoUrl = Uri.parse("http://www.pdachoice.com/me/sample_mpeg4.mp4");
    mVideoView.setVideoURI(demoUrl);

    // set a media controller
    final MediaController mc = new MediaController(getActivity());
    mVideoView.setMediaController(mc);

    mVideoView.setOnPreparedListener(this);
    
    return contentView;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    Log.d("onCreateContextMenu", "" + v.getId());

    MenuInflater inflater = getActivity().getMenuInflater();

    // inflate individual menu resource respectfully
    switch (v.getId()) {
    case R.id.textViewLabel:
      // inflater.inflate(R.menu.contextmenu, menu);
      // break;
    case R.id.buttonAction:
      inflater.inflate(R.menu.contextmenu, menu);
      break;
    }
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    Log.d("ScreenOneFragment", "onContextItemSelected: " + item.getItemId());
    switch (item.getItemId()) {
    case R.id.contextMenuAction1:
      // TODO: do something ...
      break;
    case R.id.contextMenuAction2:
      // TODO: do something ...
      break;
    case R.id.contextMenuAction3:
      // TODO: do something ...
      break;
    default:
      break;
    }

    return true;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.main, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Log.d("ScreenOneFragment", "onOptionsItemSelected: " + item.getItemId());
    switch (item.getItemId()) {
    case R.id.menuAction:
      // show iPad-like popover
      View anchorView = (View) getActivity().findViewById(R.id.menuAction);
      PopupMenu popup = new PopupMenu(getActivity(), anchorView);
      MenuInflater inflater = popup.getMenuInflater();
      inflater.inflate(R.menu.popupmenu, popup.getMenu());
      popup.show();
      popup.setOnMenuItemClickListener(this); // register handler
      return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public boolean onMenuItemClick(MenuItem item) {
    Log.d("ScreenOneFragment", "onMenuItemClick: " + item.getItemId());
    switch (item.getItemId()) {
    case R.id.popupAction1:
      // TODO: do something ...
      break;
    case R.id.popupAction2:
      // TODO: do something ...
      break;
    case R.id.popupAction3:
      // TODO: do something ...
      break;
    default:
      break;
    }

    Toast.makeText(getActivity(), item.getTitle(), Toast.LENGTH_SHORT).show();
    return true;
  }

  void scaleToFitWidth(Bitmap bitmap, ImageView imageView, int desiredWidth) {
    int w = bitmap.getWidth();
    int h = bitmap.getHeight();

    float scale = (float) desiredWidth / w;
    int scaledW = (int) (w * scale);
    int scaledH = (int) (h * scale);

    imageView.getLayoutParams().width = scaledW;
    imageView.getLayoutParams().height = scaledH;
  }

  @Override
  public void onCheckedChanged(RadioGroup group, int checkedId) {
    CharSequence label = ((RadioButton) group.findViewById(checkedId)).getText();
    Log.d("ScreenOneFragment", String.format("%s is pressed", label));
  }

  // implement OnCheckedChangeListener
  @Override
  public void onCheckedChanged(CompoundButton button, boolean isChecked) {
    mProgressBar1.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
  }

  // implement OnSeekBarChangeListener
  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    mTextViewSlider.setText(String.valueOf(progress));

    mProgressBar2.setProgress(progress);
    mProgressBar2.setSecondaryProgress(100 - progress);
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
    mSeekBar.setBackgroundColor(Color.YELLOW);
  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    mSeekBar.setBackgroundColor(Color.TRANSPARENT);
  }

  // implements OnEditorActionListener methods
  @Override
  public boolean onEditorAction(TextView textView, int actionId, KeyEvent key) {
    if (textView.getId() == R.id.editTextOneLine) {
      mEditTextFourLine.setText(mEditTextOneLine.getText());
    }
    return true;
  }

  // provide the OnClickListener implementation
  @Override
  public void onClick(View v) {
    int viewId = v.getId();
    String vw = null;
    switch (viewId) {
    case R.id.buttonAction:
      vw = "radio0";
      break;
    default:
      break;
    }

    Log.d("ScreenOneFragment", String.format("%s is pressed", vw));

  }

  // provide OnItemSelectedListener interface impl
  @Override
  public void onItemSelected(AdapterView<?> list, View item, int pos, long id) {
    Log.d("MainActivity", "onItemSelected: " + mSpinner.getSelectedItem());
    // TODO do something

  }

  @Override
  public void onNothingSelected(AdapterView<?> arg0) {
    Log.d("MainActivity", "onNothingSelected: " + mSpinner.getSelectedItem());
    // do nothing, it could happen when adapter become empty

  }

  // provide OnPreparedListener interface implementation
  @Override
  public void onPrepared(MediaPlayer mediaPlayer) {
    mProgressBar1.setVisibility(View.INVISIBLE);
    mSwitchButton.setChecked(false);

    // c. You can do more with the framework core class: MediaPlayer
    mediaPlayer.setLooping(true);
    int h = mediaPlayer.getVideoHeight();
    int w = mediaPlayer.getVideoWidth();

    Log.d("onPrepared", "w: " + w + " h: " + h);

    // d. quick workaround to eliminate an empty black view
    mVideoView.seekTo(100);
    // mVideoView.start();
  }

}