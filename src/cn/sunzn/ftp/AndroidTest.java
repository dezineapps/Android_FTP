package cn.sunzn.ftp;

import com.fonsview.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class AndroidTest extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button b = (Button) this.findViewById(R.id.buttonObj);

		b.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

				/**
				 * 建立"选择档案 Action" 的 Intent
				 */
				Intent intent = new Intent(Intent.ACTION_PICK);

				/**
				 * 过滤档案格式
				 */
				intent.setType("image/*");

				/**
				 * 建立"档案选择器" 的 Intent (第二个参数: 选择器的标题)
				 */
				Intent destIntent = Intent.createChooser(intent, "选择档案");

				/**
				 * 切换到档案选择器(它的处理结果, 会触发 onActivityResult 事件)
				 */
				startActivityForResult(destIntent, 0);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ContinueFTP myFtp = new ContinueFTP();
		try {
			super.onActivityResult(requestCode, resultCode, data);
			// 有选择档案
			if (resultCode == RESULT_OK) {
				/**
				 * 取得档案的 URI
				 */
				Uri uri = data.getData();

				if (uri != null) {
					/**
					 * 利用 URI 显示 ImageView 图片
					 */
					ImageView iv = (ImageView) this.findViewById(R.id.imageViewObj);
					iv.setImageURI(uri);
					setTitle(uri.toString());

					/**
					 * 通过URI获取到绝对路径
					 */
					String path = getAbsoluteImagePath(uri);
					
					/**
					 * 上传本地文件至远程 FTP 服务器
					 */
					myFtp.connect("192.168.20.134", 21, "sunzn", "sunzn");
					myFtp.ftpClient.makeDirectory(new String("Android".getBytes("UTF-8"), "iso-8859-1"));
					myFtp.ftpClient.changeWorkingDirectory(new String("Android".getBytes("UTF-8"), "iso-8859-1"));

					int index = path.lastIndexOf("/");
					String name = path.substring(index + 1, path.length());

					myFtp.upload(path, "/photos/" + name);
					myFtp.disconnect();

					/** 下载文件 **/
					// System.out.println(myFtp.download("/aaa/Blue hills.jpg",
					// "E:\\Blue hills.jpg"));
				} else {
					setTitle("无效的档案路径!!");
				}
			} else {
				setTitle("取消选择档案!!");
			}
		} catch (Exception e) {
			// Log.d("line: ", "aaa");
		}
	}
	
	/**
	 * 通过 URI 获取文件的绝对路径
	 * 
	 * @param uri
	 * @return
	 */
	protected String getAbsoluteImagePath(Uri uri) {
		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, proj, // Which columns to return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

}