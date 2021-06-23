package com.hiveworkshop.rms.editor.model.animflag;

import com.hiveworkshop.rms.editor.model.TimelineContainer;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxFloatTimeline;
import com.hiveworkshop.rms.util.MathUtils;

import javax.swing.*;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A java class for MDL "motion flags," such as Alpha, Translation, Scaling, or
 * Rotation. AnimFlags are not "real" things from an MDL and are given this name
 * by me, as an invented java class to simplify the programming
 *
 * Eric Theller 11/5/2011
 */
public class FloatAnimFlag extends AnimFlag<Float> {

	public FloatAnimFlag(String title) {
		super(title);
	}

	public FloatAnimFlag(AnimFlag<Float> af) {
		super(af);
	}

	public FloatAnimFlag(final MdlxFloatTimeline timeline) {
		super(timeline);

		final long[] frames = timeline.frames;
		final Object[] values = timeline.values;
		final Object[] inTans = timeline.inTans;
		final Object[] outTans = timeline.outTans;

		if (frames.length > 0) {
			final boolean hasTangents = interpolationType.tangential();

			for (int i = 0, l = frames.length; i < l; i++) {
				final Object value = values[i];
				float valueAsObject = ((float[]) value)[0];

				Float inTanAsObject = null;
				Float outTanAsObject = null;

				if (hasTangents) {
					inTanAsObject = ((float[]) inTans[i])[0];
					outTanAsObject = ((float[]) outTans[i])[0];
				}

				addEntry((int) frames[i], valueAsObject, inTanAsObject, outTanAsObject);
			}
		}
	}

	public AnimFlag<Float> getEmptyCopy(){
		FloatAnimFlag newFlag = new FloatAnimFlag(name);
		newFlag.setSettingsFrom(this);
		return newFlag;
	}
	public AnimFlag<Float> deepCopy(){
		return new FloatAnimFlag(this);
	}

	public Float cloneValue(Object value) {
		if(value instanceof Float){
			return (Float) value;
		}
		return null;
	}

	@Override
	protected Float getIdentity(int typeId) {
		if ((typeId == ROTATION) && !entryMap.isEmpty() && entryMap.firstEntry().getValue().getValue() != null) {
			return (float) identity(ALPHA); // magic Camera rotation!
		}
		return (float) identity(typeId);
	}

	@Override
	public Float getInterpolatedValue(Integer floorTime, Integer ceilTime, float timeFactor) {
		Entry<Float> entryFloor = entryMap.get(floorTime);
		Entry<Float> entryCeil = entryMap.get(ceilTime);
		return getInterpolatedValue(entryFloor, entryCeil, timeFactor);
	}

	@Override
	public Float getInterpolatedValue(Entry<Float> entryFloor, Entry<Float> entryCeil, float timeFactor) {
		Float floorValue = entryFloor.getValue();
		Float floorOutTan = entryFloor.getOutTan();

		Float ceilValue = entryCeil.getValue();
		Float ceilInTan = entryCeil.getInTan();

		if (typeid == ALPHA) {
			return switch (interpolationType) {
				case BEZIER -> MathUtils.bezier(floorValue, floorOutTan, ceilInTan, ceilValue, timeFactor);
				case DONT_INTERP -> floorValue;
				case HERMITE -> MathUtils.hermite(floorValue, floorOutTan, ceilInTan, ceilValue, timeFactor);
				case LINEAR -> MathUtils.lerp(floorValue, ceilValue, timeFactor);
			};
		}
		throw new IllegalStateException();
	}

	@Override
	public MdlxFloatTimeline toMdlx(final TimelineContainer container) {
		final MdlxFloatTimeline mdlxTimeline = new MdlxFloatTimeline();

		mdlxTimeline.name = FlagUtils.getWar3ID(name, container);
		mdlxTimeline.interpolationType = interpolationType;
		mdlxTimeline.globalSequenceId = getGlobalSeqId();

		long[] tempFrames = new long[entryMap.size()];
		float[][] tempValues = new float[entryMap.size()][];
		float[][] tempInTans = new float[entryMap.size()][];
		float[][] tempOutTans = new float[entryMap.size()][];

		boolean hasTangents = mdlxTimeline.interpolationType.tangential();

		for (int i = 0, l = entryMap.size(); i < l; i++) {
			Float value = getValueFromIndex(i);
			tempFrames[i] = getTimeFromIndex(i);

			tempValues[i] = new float[] {value};

			if (hasTangents) {
				tempInTans[i] = new float[] {getInTanFromIndex(i)};
				tempOutTans[i] = new float[] {getOutTanFromIndex(i)};
			} else {
				tempInTans[i] = new float[] {0};
				tempOutTans[i] = new float[] {0};
			}
		}

		mdlxTimeline.frames = tempFrames;
		mdlxTimeline.values = tempValues;
		mdlxTimeline.inTans = tempInTans;
		mdlxTimeline.outTans = tempOutTans;

		return mdlxTimeline;
	}

	public FloatAnimFlag getMostVisible(final FloatAnimFlag partner) {
		if (partner != null) {
			if ((typeid == 0) && (partner.typeid == 0)) {

//				FloatAnimFlag self = this;
//				FloatAnimFlag mostVisible = getMostVissibleAnimFlag(self, partner, null);
//				if (mostVisible == null) return null;
//
//				// partner has priority!
//				return getMostVissibleAnimFlag(partner, self, mostVisible);
				return getMostVissibleAnimFlag(this, partner, null);

			} else {
				JOptionPane.showMessageDialog(null,
						"Error: Program attempted to compare visibility with non-visibility animation component." +
								"\nThis... probably means something is horribly wrong. Save your work, if you can.");
			}
		}
		return null;
	}

	private FloatAnimFlag getMostVissibleAnimFlag(FloatAnimFlag aFlag, FloatAnimFlag bFlag, FloatAnimFlag mostVisible) {
		final TreeMap<Integer, Entry<Float>> aEntryMap = aFlag.entryMap;
		final TreeMap<Integer, Entry<Float>> bEntryMap = bFlag.entryMap;

		TreeSet<Integer> timeSet = new TreeSet<>();
		timeSet.addAll(aEntryMap.keySet());
		timeSet.addAll(bEntryMap.keySet());

		for (int time : timeSet) {
			Float aVal = aFlag.valueAt(time);
			Float bVal = bFlag.valueAt(time);

			if (bVal == null && aVal != null && aVal < 1
					|| aVal != null && bVal != null && bVal > aVal) {
				if (mostVisible == null) {
					mostVisible = bFlag;
				} else if (mostVisible == aFlag) {
					return null;
				}
			} else if (aVal == null && bVal != null && bVal < 1
					|| aVal != null && bVal != null && bVal < aVal) {
				if (mostVisible == null) {
					mostVisible = aFlag;
				} else if (mostVisible == bFlag) {
					return null;
				}
			}
		}

//
//		for (int time = aEntryMap.ceilingKey(aEntryMap.firstKey()); time <= aEntryMap.floorKey(aEntryMap.lastKey()); time = aEntryMap.higherKey(time)) {
//			Float aVal = aEntryMap.get(time).getValue();
//			if(bEntryMap.containsKey(time)){
//				Float bVal = bEntryMap.get(time).getValue();
//				if (bVal > aVal) {
//					if (mostVisible == null) {
//						mostVisible = bFlag;
//					} else if (mostVisible == aFlag) {
//						return null;
//					}
//				} else if (bVal < aVal) {
//					if (mostVisible == null) {
//						mostVisible = aFlag;
//					} else if (mostVisible == bFlag) {
//						return null;
//					}
//				}
//			} else if (aVal < 1) {
//				if (mostVisible == null) {
//					mostVisible = bFlag;
//				} else if (mostVisible == aFlag) {
//					return null;
//				}
//			}
//		}

		return mostVisible;
	}

	@Override
	public float[] getTbcFactor(float bias, float tension, float continuity) {
		return getTCB(-1, bias, tension, continuity);
	}

	@Override
	public void calcNewTans(float[] factor, Entry<Float> next, Entry<Float> prev, Entry<Float> cur, int animationLength) {
		// Calculating the derivatives in point Cur (for count cells)

		float currPrev = cur.value - prev.value;
		float nextCurr = next.value - cur.value;

		cur.inTan = currPrev * factor[0] + nextCurr * factor[1];
		cur.outTan = currPrev * factor[2] + nextCurr * factor[3];

		if (!next.time.equals(prev.time)) {
			float timeBetweenFrames = (next.time - prev.time + animationLength) % animationLength;
			int timeToPrevFrame = (cur.time - prev.time + animationLength) % animationLength;
			int timeToNextFrame = (next.time - cur.time + animationLength) % animationLength;

			float inAdj = 2 * timeToPrevFrame / timeBetweenFrames;
			float outAdj = 2 * timeToNextFrame / timeBetweenFrames;
			cur.inTan *= inAdj;
			cur.outTan *= outAdj;
		}
	}
}
