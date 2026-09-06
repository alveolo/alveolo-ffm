#include <stdint.h>
#include <stddef.h>
#include <errno.h>
#include <stdarg.h>
#include <wchar.h>

#ifdef _WIN32
#define EXPORT __declspec(dllexport)
#else
#define EXPORT
#endif

typedef struct pair {
  int left;
  int right;
} pair;

typedef struct pair_box_value {
  pair pair;
} pair_box_value;

typedef struct pair_box_ptr {
  pair* pair;
} pair_box_ptr;

typedef struct native_arrays {
  int32_t matrix[2][3];
  pair points[2];
  pair* pointers[2];
} native_arrays;

typedef struct int3_value {
  int32_t values[3];
} int3_value;

static void virtual_fill(void* self, int32_t* values, int32_t count) {
  for (int32_t i = 0; i < count; i++) {
    values[i] = 10 + i;
  }
}

// Count validation tests must remain safe even if Java validation regresses.
static int32_t virtual_count(void* self, int32_t* values, int32_t count) {
  return count;
}

typedef struct counted_vtable {
  void (*fill_array)(void*, int32_t*, int32_t);
  void (*fill_buffer)(void*, int32_t*, int32_t);
  int32_t (*count)(void*, int32_t*, int32_t);
} counted_vtable;

typedef struct counted_virtual {
  const counted_vtable* vtable;
} counted_virtual;

EXPORT const counted_virtual* get_counted_virtual(void) {
  static const counted_vtable vtable = {
    virtual_fill, virtual_fill, virtual_count
  };
  static const counted_virtual object = {&vtable};
  return &object;
}

EXPORT int add_ints(int left, int right) {
  return left + right;
}

EXPORT long echo_slong(long value) {
  return value;
}

EXPORT unsigned long echo_ulong(unsigned long value) {
  return value;
}

EXPORT size_t echo_size_t(size_t value) {
  return value;
}

EXPORT size_t read_size_t(const size_t* value) {
  return *value;
}

EXPORT size_t sum_size_t(const size_t* first, const size_t* second) {
  return *first + *second;
}

EXPORT const size_t* size_t_address(void) {
  static const size_t value = (size_t)-1;
  return &value;
}

EXPORT wchar_t echo_wchar(wchar_t value) {
  return value;
}

EXPORT long read_c_long(const long* value) {
  return *value;
}

EXPORT long* c_long_address(void) {
  static long value = 123;
  return &value;
}

EXPORT int variadic_sum(int count, ...) {
  va_list arguments;
  va_start(arguments, count);

  int result = 0;
  for (int i = 0; i < count; i++) {
    result += va_arg(arguments, int);
  }

  va_end(arguments);
  return result;
}

EXPORT int set_errno_and_return(int value, int error) {
  errno = error;
  return value;
}

EXPORT int64_t utf8_bytes(const char* value) {
  int64_t count = 0;
  while (value[count] != '\0') {
    count++;
  }
  return count;
}

EXPORT pair make_pair(int left, int right) {
  pair result = {left, right};
  return result;
}

EXPORT pair make_pair_and_set_errno(int left, int right, int error) {
  errno = error;
  pair result = {left, right};
  return result;
}

static pair virtual_make_pair(void* self, int left, int right) {
  return make_pair(left, right);
}

static pair virtual_make_pair_with_error(
    void* self, int left, int right, int error) {
  return make_pair_and_set_errno(left, right, error);
}

typedef struct virtual_pairs_vtable {
  pair (*make)(void*, int, int);
  pair (*make_with_error)(void*, int, int, int);
} virtual_pairs_vtable;

typedef struct virtual_pairs {
  const virtual_pairs_vtable* vtable;
} virtual_pairs;

EXPORT const virtual_pairs* get_virtual_pairs(void) {
  static const virtual_pairs_vtable vtable = {
    virtual_make_pair, virtual_make_pair_with_error
  };
  static const virtual_pairs object = {&vtable};
  return &object;
}

EXPORT int pair_sum(pair value) {
  return value.left + value.right;
}

EXPORT int pair_ptr_sum(const pair* value) {
  return value->left + value->right;
}

EXPORT int pair_box_value_sum(pair_box_value value) {
  return pair_sum(value.pair);
}

EXPORT int pair_box_ptr_sum(pair_box_ptr value) {
  return pair_ptr_sum(value.pair);
}

EXPORT void scale_ints(int32_t* values, int32_t count, int32_t factor) {
  for (int32_t i = 0; i < count; i++) {
    values[i] *= factor;
  }
}

EXPORT void offset_pairs(pair* values, int32_t count, int32_t delta) {
  for (int32_t i = 0; i < count; i++) {
    values[i].left += delta;
    values[i].right += delta;
  }
}

EXPORT void fill_pairs(pair* values, int32_t count, int32_t start) {
  for (int32_t i = 0; i < count; i++) {
    values[i].left = start + i * 2;
    values[i].right = start + i * 2 + 1;
  }
}

EXPORT void mutate_native_arrays(native_arrays* values) {
  values->matrix[1][2] += 100;
  values->points[1].left += 10;
  values->points[1].right += 20;
  if (values->pointers[0] != 0) {
    values->pointers[0]->left += 30;
    values->pointers[0]->right += 40;
  }
  if (values->pointers[1] == 0) {
    values->matrix[0][1] = 77;
  }
}

EXPORT int32_t sum_three_and_clobber(int32_t* values) {
  int32_t result = values[0] + values[1] + values[2];
  values[0] = 777;
  return result;
}

EXPORT void fill_two_ints(int32_t* values, int32_t start) {
  values[0] = start;
  values[1] = start + 1;
}

EXPORT void increment_bytes(uint8_t* values, int32_t count) {
  for (int32_t i = 0; i < count; i++) {
    values[i] += 1;
  }
}

EXPORT int32_t sum_int3_value(int3_value value) {
  int32_t result = value.values[0] + value.values[1] + value.values[2];
  value.values[0] = 777;
  return result;
}
