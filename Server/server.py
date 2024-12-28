from flask import Flask, request, send_file, jsonify
from rembg import remove
from PIL import Image
import io
import os
import datetime
import re
from flask_cors import CORS
from werkzeug.utils import secure_filename
import logging

app = Flask(__name__)
CORS(app)

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

BASE_DIR = os.path.dirname(__file__)
UPLOAD_FOLDER = os.path.join(BASE_DIR, 'user_images')
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def is_valid_firebase_uid(uid):
    """Validate Firebase UID format"""
    return bool(re.match(r'^[a-zA-Z0-9]{28}$', uid))


def ensure_user_directory(user_id):
    """Create a directory for the user if it doesn't exist"""
    if not user_id or not is_valid_firebase_uid(user_id):
        raise ValueError("Invalid user ID")

    user_dir = os.path.join(UPLOAD_FOLDER, str(user_id))
    if not os.path.exists(user_dir):
        os.makedirs(user_dir)
    return user_dir


@app.route('/remove-background', methods=['POST'])
def remove_background():
    try:
        # Validate user ID
        user_id = request.form.get('user_id')
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400

        # Validate image file
        if 'image' not in request.files:
            return jsonify({'error': 'No image file provided'}), 400

        file = request.files['image']
        if file.filename == '':
            return jsonify({'error': 'No selected file'}), 400

        if file and allowed_file(file.filename):
            try:
                # Process image
                input_image = Image.open(file.stream)
                output_image = remove(input_image)

                # Trả ảnh đã xử lý nhưng chưa lưu
                temp_buffer = io.BytesIO()
                output_image.save(temp_buffer, format='PNG')
                temp_buffer.seek(0)

                return send_file(
                    temp_buffer,
                    mimetype='image/png',
                    as_attachment=False
                )
            except Exception as e:
                logger.error(f"Image processing error: {str(e)}")
                return jsonify({'error': 'Error processing image'}), 500
        else:
            return jsonify({'error': 'Invalid file type'}), 400

    except Exception as e:
        logger.error(f"Unexpected error in remove_background: {str(e)}")
        return jsonify({'error': str(e)}), 500


@app.route('/save-image', methods=['POST'])
def save_image():
    try:
        # Validate user ID
        user_id = request.form.get('user_id')
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user I9D'}), 400

        # Validate image file
        if 'image' not in request.files:
            return jsonify({'error': 'No image file provided'}), 400

        file = request.files['image']
        if file.filename == '':
            return jsonify({'error': 'No selected file'}), 400


        if file and allowed_file(file.filename):
            try:
                user_dir = ensure_user_directory(user_id)

                # Generate safe filename and save
                timestamp = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
                safe_filename = secure_filename(f"processed_{timestamp}_{file.filename}")
                file_path = os.path.join(user_dir, safe_filename)

                # Save image
                input_image = Image.open(file.stream)
                input_image.save(file_path, 'PNG')

                return jsonify({'message': 'Image saved successfully', 'filename': safe_filename}), 200
            except Exception as e:
                logger.error(f"Image saving error: {str(e)}")
                return jsonify({'error': 'Error saving image'}), 500
        else:
            return jsonify({'error': 'Invalid file type'}), 400

    except Exception as e:
        logger.error(f"Unexpected error in save_image: {str(e)}")
        return jsonify({'error': str(e)}), 500


@app.route('/get-user-images', methods=['GET'])
def get_user_images():
    try:
        user_id = request.args.get('user_id')
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400

        user_dir = os.path.join(UPLOAD_FOLDER, str(user_id))

        if not os.path.exists(user_dir):
            return jsonify([])

        image_files = []
        for filename in os.listdir(user_dir):
            if allowed_file(filename):
                image_url = f"/images/{user_id}/{filename}"
                image_files.append(image_url)

        return jsonify(image_files)

    except Exception as e:
        logger.error(f"Error in get_user_images: {str(e)}")
        return jsonify({'error': str(e)}), 500


@app.route('/images/<user_id>/<filename>')
def serve_image(user_id, filename):
    try:
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400

        file_path = os.path.join(UPLOAD_FOLDER, str(user_id), filename)
        if not os.path.exists(file_path):
            return jsonify({'error': 'Image not found'}), 404

        return send_file(file_path, mimetype='image/png')
    except Exception as e:
        logger.error(f"Error serving image: {str(e)}")
        return jsonify({'error': str(e)}), 404


@app.route('/delete-images', methods=['POST'])
def delete_images():
    try:
        user_id = request.json.get('user_id')
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400

        filenames = request.json.get('filenames')
        if not filenames or not isinstance(filenames, list):
            return jsonify({'error': 'Invalid filenames provided'}), 400

        user_dir = os.path.join(UPLOAD_FOLDER, str(user_id))
        if not os.path.exists(user_dir):
            return jsonify({'error': 'User directory not found'}), 404

        deleted_files = []
        failed_files = []

        for filename in filenames:
            if '..' in filename or '/' in filename:
                failed_files.append(filename)
                continue

            file_path = os.path.join(user_dir, filename)

            if os.path.exists(file_path) and os.path.commonpath([file_path, user_dir]) == user_dir:
                try:
                    os.remove(file_path)
                    deleted_files.append(filename)
                except Exception as e:
                    logger.error(f"Error deleting file {filename}: {str(e)}")
                    failed_files.append(filename)
            else:
                failed_files.append(filename)

        return jsonify({
            'success': True,
            'deleted_files': deleted_files,
            'failed_files': failed_files
        })

    except Exception as e:
        logger.error(f"Error in delete_images: {str(e)}")
        return jsonify({'error': str(e)}), 500


if __name__ == '__main__':
    if not os.path.exists(UPLOAD_FOLDER):
        os.makedirs(UPLOAD_FOLDER)

    app.run(host='0.0.0.0', port=5001, debug=True)