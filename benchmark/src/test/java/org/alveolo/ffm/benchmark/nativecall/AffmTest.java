package org.alveolo.ffm.benchmark.nativecall;

import java.lang.foreign.SegmentAllocator;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.ForeignName;
import org.alveolo.ffm.In;
import org.alveolo.ffm.Library;
import org.alveolo.ffm.Out;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.Value;

@Library("affm_test")
@ForeignInterface
public interface AffmTest {
  int add_ints(int left, int right);

  long utf8_bytes(String value);

  @ForeignName("make_pair")
  PairR make_pair_record(int left, int right);

  int pair_sum(PairR value);

  @ForeignName("pair_ptr_sum")
  int pair_ptr_sum_record(@Address PairR ref);

  @ForeignName("make_pair")
  @Value PairS make_pair(
      SegmentAllocator allocator, int left, int right);

  @ForeignName("pair_sum")
  int pair_sum_interface_value(@Value PairS value);

  @ForeignName("pair_ptr_sum")
  int pair_ptr_sum_interface(PairS ref);

  @ForeignName("pair_box_value_sum")
  int pair_box_record_value_sum(PairBoxRV value);

  @ForeignName("pair_box_ptr_sum")
  int pair_box_record_address_sum(PairBoxRA value);

  @ForeignName("pair_box_ptr_sum")
  int pair_box_interface_address_sum(PairBoxIA value);

  @ForeignName("pair_box_value_sum")
  int pair_box_interface_value_sum(PairBoxIV value);

  void scale_ints(int[] values, int count, int factor);

  @ForeignName("sum_three_and_clobber")
  int sum_three_and_clobber(@In @Sequence(3) int[] values);

  @ForeignName("fill_two_ints")
  void fill_two_ints(@Out @Sequence(2) int[] values, int start);

  void increment_bytes(ByteBuffer values, int count);

  @ForeignName("fill_two_ints")
  void fill_two_int_buffer(@Out @Sequence(2) IntBuffer values, int start);
}
