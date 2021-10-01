
public class Car {
	
		private String model;
		private int year;

		public Car(String model, int year) {
			if(model.equals("") || year <1900) {
				model = null; year = 0;
			}
			this.model = model;
			this.year = year;
		}
		public String getModel() {
			return this.model;
		}
		public int getYear() {
			return this.year;
		}
	
}
