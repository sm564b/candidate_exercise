
public class Part{
		private String name;
		private String type;
		private String carModel;
		public Part(String name, String type, String carModel) {
			if(name.equals("") || type.equals("") || carModel.equals("")) {
				name = null; type = null; carModel = null;
			}
			this.name = name; this.type = type; this.carModel = carModel;
		}

		
		public String getName() {
			return this.name;
		}
		public String getType() {
			return this.type;
		}
		public String getModel() {
			return this.carModel;
		}
	}