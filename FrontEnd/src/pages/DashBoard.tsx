const DashBoard = () => {
  return (
    <div className="flex md:flex-row flex-col-reverse h-full p-5 md:space-x-5 space-x-0">
      <div className="md:w-1/2 md:h-full h-1/2 md:mb-0 mb-5 bg-yellow-100">
        left container
      </div>
      <div className="md:w-1/2 md:h-full h-1/2 md:mb-0 mb-5 bg-green-100">
        right container
      </div>
    </div>
  );
};

export default DashBoard;
