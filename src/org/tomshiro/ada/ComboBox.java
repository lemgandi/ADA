package org.tomshiro.ada;

/*
 * ComboBox courtesy of StackOverflow user Reneez (1219456)
 */
import java.util.ArrayList;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.content.res.TypedArray;
import android.util.Log;


public class ComboBox extends LinearLayout {
   static final String TAG="ComboBox";
   
   private AutoCompleteTextView _text;
   private ImageButton _button;
   private Context my_context;

   public ComboBox(Context context) {
       super(context);
       Log.d(TAG,"Create context");
       this.createChildControls(context,0);
       my_context=context;
   }

   public ComboBox(Context context, AttributeSet attrs) {
	   
	   
       super(context, attrs);
       Log.d(TAG,"Create context,attrs");
       Log.d(TAG,"Attributeset size="+attrs.getAttributeCount());
       
       TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.ComboBox);
       int text_id;       
       text_id=a.getResourceId(R.styleable.ComboBox_internalId,0);
       a.recycle();
       Log.d(TAG,"ComboBox_internalId = ["+text_id+"]");
       this.createChildControls(context,text_id);
       my_context=context;
}

 private void createChildControls(Context context,int internalId) {
    this.setOrientation(HORIZONTAL);
    this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                   LayoutParams.WRAP_CONTENT));

   _text = new AutoCompleteTextView(context);
   _text.setSingleLine();
   _text.setId(internalId);
   _text.setTextColor(getResources().getColor(android.R.color.black));
   _text.setInputType(InputType.TYPE_CLASS_TEXT
                   | InputType.TYPE_TEXT_VARIATION_NORMAL
                   | InputType.TYPE_TEXT_FLAG_CAP_WORDS
                   | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                   | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
   _text.setRawInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
   this.addView(_text, new LayoutParams(LayoutParams.WRAP_CONTENT,
                   LayoutParams.WRAP_CONTENT, 1));

   _button = new ImageButton(context);
   _button.setImageResource(android.R.drawable.arrow_down_float);
   _button.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
                   _text.showDropDown();
           }
   });
   this.addView(_button, new LayoutParams(LayoutParams.WRAP_CONTENT,
                   LayoutParams.WRAP_CONTENT));
 }
public void setEnabled(boolean onoff)
{
	super.setEnabled(onoff);
	_text.setEnabled(onoff);
}
/**
    * Sets the source for DDLB suggestions.
    * Cursor MUST be managed by supplier!!
    * @param source Source of suggestions.
    * @param column Which column from source to show.
    */
 public void setChoiceList(String selectSql,AdaDB DBHelper) {
	 ArrayList<String> parmsList = DBHelper.runListQuery(selectSql);							
	 ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(my_context,android.R.layout.simple_spinner_dropdown_item,
				parmsList.toArray(new String[0]));
    
    _text.setAdapter(myAdapter);
 }

/**
    * Gets the text in the combo box.
    *
    * @return Text.
    */
public String getText() {
    return _text.getText().toString();
 }

/**
    * Sets the text in combo box.
    */
public void setText(String text) {
    _text.setText(text);
   }
}

