package lanr.logic.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.humble.video.AudioFormat.Type;
import lanr.logic.model.LANRException;

/**
 * Used to select the correct {@link DoubleConverter}.
 * 
 * @author Nicolas Bruch
 *
 */
public class Converter {

	private static final Map<Type, DoubleConverter> converters;
	static {
		DoubleConverter b8Conv = new Byte8BitConverter();
		DoubleConverter s16Conv = new Short16BitConverter();
		DoubleConverter i32Conv = new Int32BitConverter();
		DoubleConverter f32Conv = new Float32BitConverter();
		DoubleConverter d64Conv = new Double64BitConverter();

		Map<Type, DoubleConverter> map = new HashMap<Type, DoubleConverter>();
		map.put(Type.SAMPLE_FMT_DBL, d64Conv);
		map.put(Type.SAMPLE_FMT_DBLP, d64Conv);
		map.put(Type.SAMPLE_FMT_FLT, f32Conv);
		map.put(Type.SAMPLE_FMT_FLTP, f32Conv);
		map.put(Type.SAMPLE_FMT_S16, s16Conv);
		map.put(Type.SAMPLE_FMT_S16P, s16Conv);
		map.put(Type.SAMPLE_FMT_S32, i32Conv);
		map.put(Type.SAMPLE_FMT_S32P, i32Conv);
		map.put(Type.SAMPLE_FMT_U8, b8Conv);
		map.put(Type.SAMPLE_FMT_U8P, b8Conv);
		converters = Collections.unmodifiableMap(map);
	}

	/**
	 * Returns the correct {@link DoubleConverter} for a given {@link Type} of audio.
	 * @param audioFormat - Format type of audio data.
	 * @return Double converter to convert audio data.
	 * @throws LANRException If no converter was found for the given format.
	 */
	public static DoubleConverter getConverter(Type audioFormat) throws LANRException {
		DoubleConverter converter = converters.get(audioFormat);
		if (converter == null) {
			throw new LANRException("Unsupported audio format: " + audioFormat.name());
		}
		return converter;
	}
}
