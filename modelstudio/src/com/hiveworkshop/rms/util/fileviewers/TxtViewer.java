package com.hiveworkshop.rms.util.fileviewers;

import javax.swing.text.StyledEditorKit;
import java.io.*;

public class TxtViewer extends FileViewer {
	private static final String utf8Bom = String.valueOf(new char[] {0xEF, 0xBB, 0xBF});

	public TxtViewer() {
		super(new StyledEditorKit());
	}

	protected String getReadFile(File file) {
		try (FileInputStream in = new FileInputStream(file)) {
			return readStream(in);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	protected String readStream(InputStream in) {
		StringBuilder stringBuilder = new StringBuilder();

		try (BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
			char[] tempChar = new char[3];
			r.mark(4);
			if (r.read(tempChar, 0, 3) != 3 || !utf8Bom.equals(new String(tempChar))) {
				r.reset();
			}
			r.lines().forEach(l -> stringBuilder.append(l).append("\n"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return stringBuilder.toString();
	}
}