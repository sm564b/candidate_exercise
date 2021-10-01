
public class Feature{
		private String name;
		private String car_model;

		public Feature(String name, String car_model) {
			if(name.equals("") || car_model.equals("")) {
				name = null; car_model = null;
			}
			this.name = name; this.car_model = car_model;
		}

		public String getFeaturename() {
			return this.name;
		}
		public String getCarModel() {
			return this.car_model;
		}
	}
