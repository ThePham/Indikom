package slackClient;

public class MessageObject {
	String message;
	boolean color;
	boolean showMessage;

	boolean first_m_words;
	boolean first_m_words_key_word;
	boolean whole_msg;
	boolean n_th_msg;
	boolean not_nth_msg;
	boolean work_context;
	boolean key_word;
	boolean sentence_type;
	

	public MessageObject(){
		this.message = "";
		this.color = false;
		this.showMessage = false;
		this.first_m_words = false;
		this.first_m_words_key_word = false;
		this.whole_msg = false;
		this.n_th_msg = false;
		this.not_nth_msg = false;
		this.work_context = false;
		this.key_word = false;
		this.sentence_type = false;
	}
	
	public MessageObject(String message, boolean color, boolean showMessage){
		this.message = message;
		this.color = color;
		this.showMessage = showMessage;
		
		this.first_m_words = false;
		this.first_m_words_key_word = false;
		this.whole_msg = false;
		this.n_th_msg = false;
		this.not_nth_msg = false;
		this.work_context = false;
		this.key_word = false;
		this.sentence_type = false;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean getColor() {
		return color;
	}

	public void setColor(boolean color) {
		this.color = color;
	}

	public boolean getShowMessage() {
		return showMessage;
	}

	public void setShowMessage(boolean showMessage) {
		this.showMessage = showMessage;
	}

	public boolean getFirst_m_words() {
		return first_m_words;
	}

	public void setFirst_m_words(boolean first_m_words) {
		this.first_m_words = first_m_words;
	}

	public boolean getFirst_m_words_key_word() {
		return first_m_words_key_word;
	}

	public void setFirst_m_words_key_word(boolean first_m_words_key_word) {
		this.first_m_words_key_word = first_m_words_key_word;
	}

	public boolean getWhole_msg() {
		return whole_msg;
	}

	public void setWhole_msg(boolean whole_msg) {
		this.whole_msg = whole_msg;
	}

	public boolean getN_th_msg() {
		return n_th_msg;
	}

	public void setN_th_msg(boolean n_th_msg) {
		this.n_th_msg = n_th_msg;
	}

	public boolean getNot_nth_msg() {
		return not_nth_msg;
	}

	public void setNot_nth_msg(boolean not_nth_msg) {
		this.not_nth_msg = not_nth_msg;
	}

	public boolean getWork_context() {
		return work_context;
	}

	public void setWork_context(boolean work_context) {
		this.work_context = work_context;
	}

	public boolean getKey_word() {
		return key_word;
	}

	public void setKey_word(boolean key_word) {
		this.key_word = key_word;
	}

	public boolean getSentence_type() {
		return sentence_type;
	}

	public void setSentence_type(boolean sentence_type) {
		this.sentence_type = sentence_type;
	}

}