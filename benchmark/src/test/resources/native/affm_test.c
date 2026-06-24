#include <stdint.h>

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

EXPORT int add_ints(int left, int right) {
  return left + right;
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
