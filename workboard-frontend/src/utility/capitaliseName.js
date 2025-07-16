const capitaliseName = (name) => {
  if (typeof name !== "string" || name.length === 0) {
    return name; // Handle non-string input or empty strings
  }
  return name.charAt(0).toUpperCase() + name.slice(1);
};
export default capitaliseName;
