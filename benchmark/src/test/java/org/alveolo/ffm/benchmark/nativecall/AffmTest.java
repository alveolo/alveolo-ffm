package org.alveolo.ffm.benchmark.nativecall;

import java.lang.foreign.SegmentAllocator;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.FirstVariadicArg;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.In;
import org.alveolo.ffm.Library;
import org.alveolo.ffm.Out;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.SLong;
import org.alveolo.ffm.SizeT;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.ULong;
import org.alveolo.ffm.Value;
import org.alveolo.ffm.WCharT;

@Library("affm_test")
@ForeignInterface
public interface AffmTest {
  ArrayVirtual get_array_virtual();

  VirtualPairs get_virtual_pairs();

  int add_ints(int left, int right);

  @Symbol("null_pointer") PairS absentPair();
  @Symbol("null_pointer") NullableUnion absentUnion();
  @Symbol("null_pointer") PairSFM absentWrapper();
  @Symbol("null_pointer") @Address PairR absentRecord();
  @Symbol("null_pointer") VirtualPairs absentVirtual();
  @Symbol("null_pointer") String absentString();
  @Symbol("null_pointer") @Address int absentInt();
  @Symbol("null_pointer") @Address @SizeT long absentSize();

  @Symbol("nullable_pair") int optionalPair(PairS value);
  @Symbol("nullable_pair") int optionalWrapper(PairSFM value);
  @Symbol("nullable_pair") int optionalRecord(@Address PairR value);
  @Symbol("nullable_string") int optionalString(String value);
  @Symbol("nullable_values")
  int optionalValues(@Address PairR value, String text);
  @Symbol("required_values")
  int requiredValues(PairR value, String text);
  @Symbol("nullable_values")
  int optionalInterfaceValues(PairS value, String text);
  @Symbol("nullable_box")
  int optionalAllocatingValues(@Address PairBoxRA value, String text);


  @SLong long echo_slong(@SLong long value);

  @ULong long echo_ulong(@ULong long value);

  @SizeT long echo_size_t(@SizeT long value);

  @SizeT long read_size_t(@Address @SizeT long value);

  @SizeT long sum_size_t(
      @Address @SizeT long first, @Address @SizeT long second);

  @Address @SizeT long size_t_address();

  @WCharT int echo_wchar(@WCharT int value);

  @Symbol("read_c_long")
  @SLong long read_c_long(@Address @SLong long value);

  @Address @SLong long c_long_address();

  @FirstVariadicArg(1)
  int variadic_sum(int count);

  @FirstVariadicArg(1)
  int variadic_sum(int count, int first, int second, int third);

  long utf8_bytes(String value);

  int set_errno_and_return(ErrnoSpec capture, int value, int error);

  default int checked_errno_return(
      ErrnoSpec capture, int value, int error) {
    int result = set_errno_and_return(capture, value, error);
    capture.throwIf(() -> result == -1);
    return result;
  }

  @Symbol("make_pair")
  PairR make_pair_record(int left, int right);

  PairR make_pair_and_set_errno(
      ErrnoSpec capture, int left, int right, int error);

  int pair_sum(PairR value);

  @Symbol("pair_ptr_sum")
  int pair_ptr_sum_record(@Address PairR ref);

  @Symbol("make_pair")
  @Value
  PairS make_pair(
      SegmentAllocator allocator, int left, int right);

  @Symbol("pair_sum")
  int pair_sum_interface_value(@Value PairS value);

  @Symbol("pair_ptr_sum")
  int pair_ptr_sum_interface(PairS ref);

  @Symbol("pair_box_value_sum")
  int pair_box_record_value_sum(PairBoxRV value);

  @Symbol("pair_box_ptr_sum")
  int pair_box_record_address_sum(PairBoxRA value);

  @Symbol("pair_box_ptr_sum")
  int pair_box_interface_address_sum(@Value PairBoxIA value);

  @Symbol("pair_box_value_sum")
  int pair_box_interface_value_sum(@Value PairBoxIV value);

  void scale_ints(
      int[] values, int count, int factor);

  default void scale_ints(int[] values, int factor) {
    scale_ints(values, values.length, factor);
  }

  void offset_pairs(
      PairR[] values, int count, int delta);

  default void offset_pairs(PairR[] values, int delta) {
    offset_pairs(values, values.length, delta);
  }

  void fill_pairs(
      @Out PairR[] values, int count, int start);

  default void fill_pairs(PairR[] values, int start) {
    fill_pairs(values, values.length, start);
  }

  void mutate_native_arrays(NativeArrays value);

  @Symbol("sum_three_and_clobber")
  int sum_three_and_clobber(@In @Sequence(3) int[] values);

  @Symbol("fill_two_ints")
  void fill_two_ints(@Out @Sequence(2) int[] values, int start);

  void increment_bytes(
      ByteBuffer values, int count);

  default void increment_bytes(ByteBuffer values) {
    increment_bytes(values, values.remaining());
  }

  @Symbol("fill_two_ints")
  void fill_two_int_buffer(@Out @Sequence(2) IntBuffer values, int start);

  @Symbol("scale_ints")
  void scale_int_buffer(
      IntBuffer values, int count, int factor);

  default void scale_int_buffer(IntBuffer values, int factor) {
    scale_int_buffer(values, values.remaining(), factor);
  }

  @Symbol("sum_three_and_clobber")
  int sum_three_int_buffer(@In @Sequence(3) IntBuffer values);

  int sum_int3_value(@Value @Sequence(3) int[] values);

  @Symbol("sum_int3_value")
  int sum_int3_buffer_value(@Value @Sequence(3) IntBuffer values);
}
