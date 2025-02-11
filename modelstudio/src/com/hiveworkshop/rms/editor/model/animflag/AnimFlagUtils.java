package com.hiveworkshop.rms.editor.model.animflag;

import com.hiveworkshop.rms.ui.application.edit.animation.Sequence;
import com.hiveworkshop.rms.util.Vec3;

import java.util.TreeMap;

public class AnimFlagUtils {
	public static <T> void timeScale2(AnimFlag<T> animFlag, Sequence anim, int newLength, int offsetFromStart) {
		// Timescales a part of the AnimFlag from section "start" to "end" into the new time "newStart" to "newEnd"
		TreeMap<Integer, Entry<T>> entryMap = animFlag.getAnimMap().get(anim);
		if (entryMap != null) {
			TreeMap<Integer, Entry<T>> scaledMap = new TreeMap<>();
			int animLength = Math.max(0, anim.getLength());
			double ratio = (double) (newLength) / (double) animLength;
			Integer lastKF = entryMap.floorKey(animLength);
			if (lastKF != null) {
				for (Integer time = entryMap.ceilingKey(0); time != null && time <= lastKF; time = entryMap.higherKey(time)) {
					int newTime = (int) (offsetFromStart + (time * ratio));
					scaledMap.put(newTime, entryMap.remove(time).setTime(newTime));
				}
			}
			entryMap.putAll(scaledMap);
		}
	}

	public static <T> void timeScale3(AnimFlag<T> animFlag, Sequence anim, double ratio) {
		// Timescales a part of the AnimFlag from section "start" to "end" into the new time "newStart" to "newEnd"
		TreeMap<Integer, Entry<T>> entryMap = animFlag.getAnimMap().get(anim);
		if (entryMap != null) {
			scaleMapEntries(ratio, entryMap);
		}
	}

	protected static <T> void scaleMapEntries(double ratio, TreeMap<Integer, Entry<T>> entryMap) {
		TreeMap<Integer, Entry<T>> scaledMap = new TreeMap<>();
		for (Integer time : entryMap.keySet()) {
			int newTime = (int) (time * ratio);
			scaledMap.put(newTime, entryMap.get(time).setTime(newTime));
		}
		entryMap.clear();
		entryMap.putAll(scaledMap);
	}

	public static <T> void setValuesTo(AnimFlag<T> animFlag, AnimFlag<?> source) {
		//todo check it this should clear existing
		AnimFlag<T> tSource = getAsTypedOrNull(animFlag, source);
		if (tSource != null) {
			animFlag.setSettingsFrom(tSource);
			animFlag.clear();

			for (Sequence anim : tSource.getAnimMap().keySet()) {
				animFlag.setEntryMap(anim, tSource.getSequenceEntryMapCopy(anim));
//				TreeMap<Integer, Entry<T>> entryMap = tSource.getAnimMap().get(anim);
//				entryMap.replaceAll((t, v) -> tSource.getEntryMap(anim).get(t).deepCopy());
			}
		}
	}

	/**
	 * Copies time track data from a certain interval into a different, new interval.
	 * The AnimFlag source of the data to copy cannot be same AnimFlag into which the
	 * data is copied, or else a ConcurrentModificationException will be thrown.
	 * Does not check that the destination interval is empty!
	 *
	 * @param source     the AnimFlag from which values will be copied
	 * @param sourceAnim the Animation from which to copy
	 * @param newAnim    the Animation to receive Entries
	 * @param offset     the offset from the start of the receiving animation at which to start adding keyframes
	 */
	public static <T> void copyFrom(AnimFlag<T> animFlag, AnimFlag<?> source, Sequence sourceAnim, Sequence newAnim, int offset) {

		AnimFlag<T> tSource = getAsTypedOrNull(animFlag, source);
		if (tSource != null && tSource.getEntryMap(sourceAnim) != null) {
			boolean sourceHasTans = tSource.tans();

			TreeMap<Integer, Entry<T>> sequenceEntryMapCopy = tSource.getSequenceEntryMapCopy(sourceAnim);
			if (sequenceEntryMapCopy != null) {

				if (sourceAnim.getLength() + offset != newAnim.getLength()) {
					double ratio = (newAnim.getLength() - offset) / ((double) sourceAnim.getLength());
					scaleMapEntries(ratio, sequenceEntryMapCopy);
				}

				if (offset != 0) {
					TreeMap<Integer, Entry<T>> seqMovedMap = new TreeMap<>();
					sequenceEntryMapCopy.forEach((t, e) -> seqMovedMap.put(t + offset, e.setTime(t + offset)));
					sequenceEntryMapCopy = seqMovedMap;
				}

				if (!animFlag.tans() && sourceHasTans) {
					sequenceEntryMapCopy.forEach((t, e) -> e.linearize());
				} else if (animFlag.tans() && !sourceHasTans) {
					unLiniarizeMapEntries(animFlag, newAnim.getLength(), sequenceEntryMapCopy);
				}

//				TreeMap<Integer, Entry<T>> entryMap = sequenceMap.computeIfAbsent(newAnim, k -> new TreeMap<>());
//				entryMap.putAll(sequenceEntryMapCopy);
				animFlag.setEntryMap(newAnim, sequenceEntryMapCopy);
			}
		}
	}

	// Does clear existing values
	public static <T> void copyFrom(AnimFlag<T> animFlag, AnimFlag<?> source, Sequence sourceAnim, Sequence newAnim) {
		AnimFlag<T> tSource = getAsTypedOrNull(animFlag, source);
		if (tSource != null && tSource.getEntryMap(sourceAnim) != null) {
			boolean sourceHasTans = tSource.tans();

			TreeMap<Integer, Entry<T>> sequenceEntryMapCopy = tSource.getSequenceEntryMapCopy(sourceAnim);
			if (sequenceEntryMapCopy != null) {

				if (sourceAnim.getLength() != newAnim.getLength()) {
					double ratio = ((double) newAnim.getLength()) / ((double) sourceAnim.getLength());
					scaleMapEntries(ratio, sequenceEntryMapCopy);
				}

				if (!animFlag.tans() && sourceHasTans) {
					sequenceEntryMapCopy.forEach((t, e) -> e.linearize());
				} else if (animFlag.tans() && !sourceHasTans) {
					unLiniarizeMapEntries(animFlag, newAnim.getLength(), sequenceEntryMapCopy);
				}
//				sequenceMap.put(newAnim, sequenceEntryMapCopy);
				animFlag.setEntryMap(newAnim, sequenceEntryMapCopy);
			}
		}
	}

	public static <T> void copyFrom(AnimFlag<T> animFlag, AnimFlag<?> source) {
		AnimFlag<T> tSource = getAsTypedOrNull(animFlag, source);
		if (tSource != null) {
			// ToDo give user option to either linearize animflag or unlinearize copied entries
			boolean linearizeEntries = !animFlag.tans() && tSource.tans();
			boolean unlinearizeEntries = animFlag.tans() && !tSource.tans();
			for (Sequence anim : tSource.getAnimMap().keySet()) {
				TreeMap<Integer, Entry<T>> sourceEntryMap = tSource.getAnimMap().get(anim);
//				TreeMap<Integer, Entry<T>> entryMap = sequenceMap.computeIfAbsent(anim, k -> new TreeMap<>());
				TreeMap<Integer, Entry<T>> entryMap = new TreeMap<>();
				for (Integer time : sourceEntryMap.keySet()) {
					final Entry<T> copiedEntry = sourceEntryMap.get(time).deepCopy();
					if (linearizeEntries) {
						copiedEntry.linearize();
					} else if (unlinearizeEntries) {
						copiedEntry.unLinearize();
					}
					entryMap.put(time, copiedEntry);
//					addEntryMap(anim, entryMap);
					animFlag.setEntryMap(anim, entryMap);
				}
			}
		}
	}

	private static <T> AnimFlag<T> getAsTypedOrNull(AnimFlag<T> animFlag, AnimFlag<?> source) {
		if (animFlag instanceof IntAnimFlag && source instanceof IntAnimFlag
				|| animFlag instanceof FloatAnimFlag && source instanceof FloatAnimFlag
				|| animFlag instanceof Vec3AnimFlag && source instanceof Vec3AnimFlag
				|| animFlag instanceof QuatAnimFlag && source instanceof QuatAnimFlag) {
			return (AnimFlag<T>) source;
		}
		return null;
	}

	protected static <T> void unLiniarizeMapEntries(AnimFlag<T> animFlag, int animationLength, TreeMap<Integer, Entry<T>> entryTreeMap) {
		entryTreeMap.forEach((t, e) -> e.unLinearize());
		for (Integer time : entryTreeMap.keySet()) {
			Integer nextTime = entryTreeMap.higherKey(time) == null ? entryTreeMap.firstKey() : entryTreeMap.higherKey(time);
			Integer prevTime = entryTreeMap.lowerKey(time) == null ? entryTreeMap.lastKey() : entryTreeMap.lowerKey(time);


			Entry<T> prevValue = entryTreeMap.get(prevTime);
			Entry<T> nextValue = entryTreeMap.get(nextTime);

//			if(entryTreeMap.lowerKey(time) == null || entryTreeMap.higherKey(time) == null){
//				System.out.println("nextTime: " + nextTime + ", prevTime: " + prevTime);
//				System.out.println("nextValue: " + nextValue.value + ", prevValue: " + prevValue.value);
//			}

//			float[] factor = animFlag.getTbcFactor(0, 0.5f, 0);
//			float[] factor = animFlag.getTbcFactor(0, 0.75f, -.5f);
			float[] factor = animFlag.getTbcFactor(0, .2f, -.9f);
			animFlag.calcNewTans(factor, nextValue, prevValue, entryTreeMap.get(time), animationLength);
		}
	}

	public static <Q> AnimFlag<Q> createNewAnimFlag(Q defaultValue, String title){
		if(defaultValue instanceof Integer){
			return (AnimFlag<Q>) new IntAnimFlag(title);
		} else if(defaultValue instanceof Float){
			return (AnimFlag<Q>) new FloatAnimFlag(title);
		} else if(defaultValue instanceof Vec3){
			return (AnimFlag<Q>) new Vec3AnimFlag(title);
		} else if(defaultValue instanceof QuatAnimFlag){
			return (AnimFlag<Q>) new QuatAnimFlag(title);
		}

		return null;
	}
}
