import { useEffect, useState } from "react";

const useDebounce = (value, delay = 1000) => {
  const [debouncedVal, setDebouncedVal] = useState(value);

  useEffect(() => {
    const timerId = setTimeout(() => {
      // sets the debounced value after delay
      setDebouncedVal(value);
    }, delay);

    return () => {
      // cancel timeout on value change
      clearTimeout(timerId);
    };
  }, [value, delay]);

  return debouncedVal;
};

export default useDebounce;
